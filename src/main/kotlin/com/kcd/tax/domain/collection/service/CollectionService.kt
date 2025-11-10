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
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDateTime

@Service
class CollectionService(
    // repository
    private val collectionRequestRepository: CollectionRequestRepository,

    // service to service
    private val businessService: BusinessService,
    private val vatService: VatService,

    // collector
    private val collectorClient: CollectorClient
) {

    /**`
     * 매출/매입 수집 요청
     */
    @Transactional
    fun setCollectionRequest(dto: CollectionRequestReqDto) {
        val now = LocalDateTime.now()
        val business = businessService.findBusinessByRegistrationNumber(dto.registrationNumber)

        // 상/하반기 조회
        val period = findVatPeriodByYearAndHalf(dto.year, dto.halfType)

        // 최근 요청 값
        val existingRequest = findByBusinessIdAndVatPeriodIdAndIsDelete(business.id!!, period.id!!)

        // 이번 요청 전에 했던 요청은 삭제 처리
        existingRequest?.let {
            if (existingRequest.status != COLLECTED.value) throw ApiCommonException(ALREADY_COLLECTING)
            existingRequest.isDelete = true
            collectionRequestRepository.save(existingRequest)
        }

        // 수집 요청 테이블에 수집기가 요청을 받기 전 상태인 NOT_REQUESTED 저장
        val request = collectionRequestRepository.save(
            CollectionRequest.toEntity(period, business, now, NOT_REQUESTED.value)
        )

        // 수집기 비동기 처리 (트랜잭션이 커밋된 후 비동기 호출)
        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    // 트랜잭션 커밋된 다음에 호출됨
                    collectorClient.processCollectionAsync(business, request, period)
                }
            }
        )
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