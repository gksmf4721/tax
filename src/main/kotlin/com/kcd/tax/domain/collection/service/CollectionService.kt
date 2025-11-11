package com.kcd.tax.domain.collection.service

import com.kcd.tax.common.error.CommonErrorCode.*
import com.kcd.tax.common.error.exception.ApiCommonException
import com.kcd.tax.domain.business.service.BusinessService
import com.kcd.tax.domain.collection.dto.request.CollectionRequestReqDto
import com.kcd.tax.domain.collection.dto.response.CollectionStatusResDto
import com.kcd.tax.domain.collection.entity.CollectionRequest
import com.kcd.tax.domain.collection.enums.CollectionStatus
import com.kcd.tax.domain.collection.enums.CollectionStatus.*
import com.kcd.tax.domain.collection.repository.CollectionRequestRepository
import com.kcd.tax.infrastructure.CollectorClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.kcd.tax.domain.vat.entity.VatPeriod
import com.kcd.tax.domain.vat.enums.VatHalfType
import com.kcd.tax.domain.vat.service.VatService
import org.redisson.api.RedissonClient
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDateTime
import com.kcd.tax.common.enums.RedissonLockKeys.COLLECTION_LOCK
import java.util.concurrent.TimeUnit

@Service
class CollectionService(
    // repository
    private val collectionRequestRepository: CollectionRequestRepository,

    // service to service
    private val businessService: BusinessService,
    private val vatService: VatService,

    // collector
    private val collectorClient: CollectorClient,

    // lib
    private val redissonClient: RedissonClient
) {

    /**`
     * 매출/매입 수집 요청
     */
    @Transactional
    fun setCollectionRequest(dto: CollectionRequestReqDto) {
        val now = LocalDateTime.now()
        val business = businessService.findBusinessByRegistrationNumber(dto.registrationNumber)
        val period = findVatPeriodByYearAndHalf(dto.year, dto.halfType)

        // Redis 락: 사업장 기준으로 잠금
        val lock = redissonClient.getLock(COLLECTION_LOCK.getKey(business.id!!))
        val isLocked = lock.tryLock(0, 10, TimeUnit.SECONDS)
        if (!isLocked) throw ApiCommonException(ALREADY_COLLECTING)

        try {
            // 기존 요청이 있다면 삭제 처리
            val existRequest = findByBusinessIdAndVatPeriodIdAndIsDelete(business.id, period.id!!)
            existRequest?.let {
                it.isDelete = true
                collectionRequestRepository.save(it)
            }

            // 새 수집 요청 저장
            val request = collectionRequestRepository.save(
                CollectionRequest.toEntity(period, business, now, NOT_REQUESTED.value)
            )

            // 커밋 후 비동기 수집 호출
            TransactionSynchronizationManager.registerSynchronization(
                object : TransactionSynchronization {
                    override fun afterCommit() {
                        collectorClient.processCollectionAsync(business, request, period)
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
    fun findCollectionStatus(year: Int?, halfType: VatHalfType?, registrationNumber: String): CollectionStatusResDto {
        // 상/하반기 조회
        val period = findVatPeriodByYearAndHalf(year, halfType)

        val business = businessService.findBusinessByRegistrationNumber(registrationNumber)
        val collectionRequest = findByBusinessIdAndVatPeriodIdAndIsDelete(business.id!!, period.id!!)
        val status = CollectionStatus.fromValue(collectionRequest?.status ?: NOT_REQUESTED.value)

        return CollectionStatusResDto(status)
    }


    // 상/하반기에 따른 기간 ID 조회
    private fun findVatPeriodByYearAndHalf(year: Int?, halfType: VatHalfType?): VatPeriod {
        return vatService.findVatPeriodByYearAndHalf(year, halfType)
    }

    // 상/하반기에 따른 수집 요청 내역 조회
    private fun findByBusinessIdAndVatPeriodIdAndIsDelete(businessId: Long, periodId: Long): CollectionRequest? {
        return collectionRequestRepository.findByBusinessIdAndVatPeriodIdAndIsDelete(
            businessId, periodId, false
        )
    }
}