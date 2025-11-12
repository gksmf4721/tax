package com.kcd.tax.domain.collection.service

import com.kcd.tax.common.error.CommonErrorCode.*
import com.kcd.tax.common.error.exception.ApiCommonException
import com.kcd.tax.domain.collection.dto.request.CollectionRequestReqDto
import com.kcd.tax.domain.collection.dto.response.CollectionStatusResDto
import com.kcd.tax.domain.collection.entity.CollectionRequest
import com.kcd.tax.domain.collection.enums.CollectionStatus
import com.kcd.tax.domain.collection.enums.CollectionStatus.*
import com.kcd.tax.infrastructure.external.collector.CollectorClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.kcd.tax.domain.vat.enums.VatHalfType
import org.redisson.api.RedissonClient
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDateTime
import com.kcd.tax.common.enums.RedissonLockKeys.COLLECTION_LOCK
import com.kcd.tax.domain.business.client.BusinessClient
import com.kcd.tax.domain.collection.dto.request.CollectionDataReqDto
import com.kcd.tax.domain.collection.dto.response.RecordResDto
import com.kcd.tax.domain.collection.entity.PurchaseRecord
import com.kcd.tax.domain.collection.entity.SalesRecord
import com.kcd.tax.common.enums.RecordType.PURCHASE
import com.kcd.tax.common.enums.RecordType.SALES
import com.kcd.tax.domain.collection.repository.CollectionRequestRepository
import com.kcd.tax.domain.collection.repository.PurchaseRecordRepository
import com.kcd.tax.domain.collection.repository.SalesRecordRepository
import com.kcd.tax.domain.vat.client.VatClient
import com.kcd.tax.domain.vat.dto.response.VatPeriodResDto
import java.util.concurrent.TimeUnit

@Service
class CollectionService(
    // repository
    private val collectionRequestRepository: CollectionRequestRepository,
    private val salesRecordRepository: SalesRecordRepository,
    private val purchaseRecordRepository: PurchaseRecordRepository,

    // external
    private val businessClient: BusinessClient,
    private val vatClient: VatClient,

    // collector
    private val collectorClient: CollectorClient,

    // lib
    private val redissonClient: RedissonClient
) {

    /**`
     * 매출/매입 수집 요청
     */
    @Transactional
    fun setCollectionRequest(registrationNumber: String, dto: CollectionRequestReqDto) {
        val now = LocalDateTime.now()
        val business = businessClient.findBusinessByRegistrationNumber(registrationNumber)
        val businessId = business.id
        val periodId = findVatPeriodByYearAndHalf(dto.year, dto.halfType).id

        // Redis 락: 사업장 기준으로 잠금
        val lock = redissonClient.getLock(COLLECTION_LOCK.getKey(businessId))
        val isLocked = lock.tryLock(0, 10, TimeUnit.SECONDS)
        if (!isLocked) throw ApiCommonException(ALREADY_COLLECTING)

        try {
            // 기존 요청이 있다면 삭제 처리
            val existRequest = findByBusinessIdAndVatPeriodIdAndIsDelete(businessId, periodId)
            existRequest?.let {
                it.isDelete = true
                collectionRequestRepository.save(it)
            }

            // 새 수집 요청 저장
            val request = collectionRequestRepository.save(
                CollectionRequest.toEntity(periodId, businessId, now, NOT_REQUESTED.value)
            )

            // 커밋 후 비동기 수집 호출
            TransactionSynchronizationManager.registerSynchronization(
                object : TransactionSynchronization {
                    override fun afterCommit() {
                        collectorClient.processCollectionAsync(businessId, request, periodId)
                    }
                }
            )
        } finally {
            // 락 해제
            if (lock.isHeldByCurrentThread) lock.unlock()
        }
    }


    /**`
     * 매출/매입 수집 상태 조회
     */
    fun findCollectionStatus(registrationNumber: String, year: Int?, halfType: VatHalfType?): CollectionStatusResDto {
        // 상/하반기 조회
        val period = findVatPeriodByYearAndHalf(year, halfType)

        val business = businessClient.findBusinessByRegistrationNumber(registrationNumber)
        val collectionRequest = findByBusinessIdAndVatPeriodIdAndIsDelete(business.id, period.id)
        val status = CollectionStatus.fromValue(collectionRequest?.status ?: NOT_REQUESTED.value)

        return CollectionStatusResDto(status)
    }


    // 상/하반기에 따른 기간 ID 조회
    private fun findVatPeriodByYearAndHalf(year: Int?, halfType: VatHalfType?): VatPeriodResDto {
        return vatClient.findVatPeriodByYearAndHalf(year, halfType)
    }

    // 상/하반기에 따른 수집 요청 내역 조회
    private fun findByBusinessIdAndVatPeriodIdAndIsDelete(businessId: Long, periodId: Long): CollectionRequest? {
        return collectionRequestRepository.findByBusinessIdAndVatPeriodIdAndIsDelete(
            businessId, periodId, false
        )
    }

    // ===== external =====
    // 매출/매입 내역 조회
    fun findRecords(businessIds: List<Long>, periodId: Long): List<RecordResDto> {
        val purchaseRecords = purchaseRecordRepository
            .findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(businessIds, periodId)
        val salesRecords = salesRecordRepository
            .findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(businessIds, periodId)

        return RecordResDto.fromPurchase(purchaseRecords) + RecordResDto.fromSales(salesRecords)
    }

    // 매입/매출 내역 저장
    @Transactional
    fun saveAllRecords(
        businessId: Long,
        request: CollectionRequest,
        readRecords: List<CollectionDataReqDto>,
        vatPeriodId: Long
    ) {
        val now = LocalDateTime.now()

        // purchase 매입
        purchaseRecordRepository.saveAll(
            PurchaseRecord.toEntity(
                businessId = businessId,
                request = request,
                records = readRecords.filter { it.recordType == PURCHASE },
                vatPeriodId = vatPeriodId,
                now = now
            )
        )

        // sales 매출
        salesRecordRepository.saveAll(
            SalesRecord.toEntity(
                businessId = businessId,
                request = request,
                records = readRecords.filter { it.recordType == SALES },
                vatPeriodId = vatPeriodId,
                now = now
            )
        )
    }
}