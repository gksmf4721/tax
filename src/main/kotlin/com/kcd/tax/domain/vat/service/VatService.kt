package com.kcd.tax.domain.vat.service

import com.kcd.tax.common.dto.CommonParamDto
import com.kcd.tax.domain.business.service.BusinessService
import com.kcd.tax.common.enums.UserRole.*
import com.kcd.tax.common.error.CommonErrorCode.*
import com.kcd.tax.common.error.exception.ApiCommonException
import com.kcd.tax.domain.business.entity.Business
import com.kcd.tax.domain.collection.dto.request.CollectionDataReqDto
import com.kcd.tax.domain.collection.entity.CollectionRequest
import com.kcd.tax.domain.collection.enums.RecordType.PURCHASE
import com.kcd.tax.domain.collection.enums.RecordType.SALES
import com.kcd.tax.domain.vat.dto.response.VatDetailResDto
import com.kcd.tax.domain.vat.entity.PurchaseRecord
import com.kcd.tax.domain.vat.entity.SalesRecord
import com.kcd.tax.domain.vat.entity.VatPeriod
import com.kcd.tax.domain.vat.enums.VatHalfType
import com.kcd.tax.domain.vat.enums.VatHalfType.FIRST
import com.kcd.tax.domain.vat.enums.VatHalfType.SECOND
import com.kcd.tax.domain.vat.repository.PurchaseRecordRepository
import com.kcd.tax.domain.vat.repository.SalesRecordRepository
import com.kcd.tax.domain.vat.repository.VatPeriodRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt

@Service
class VatService(
    // repository
    private val salesRecordRepository: SalesRecordRepository,
    private val purchaseRecordRepository: PurchaseRecordRepository,
    private val vatPeriodRepository: VatPeriodRepository,

    // service to service
    private val businessService: BusinessService
) {

    @Value("\${vat.rate-divisor}")
    lateinit var rateDivisor: String

    @Value("\${vat.round-unit}")
    lateinit var roundUnit: String

    /**`
     * 부가세 조회
     */
    fun findVatByBusiness(year: Int?, halfType: VatHalfType?, common: CommonParamDto): List<VatDetailResDto> {
        // 권한에 따른 사업장 ID 조회
        val businesses = when (common.userRole) {
            ADMIN -> businessService.findBusinessAll()
            MANAGER -> businessService.findBusinessByUserId(common.userId!!)
        }

        // 사업장 IDS
        val businessIds = businesses.mapNotNull { it.id }

        // 조회 기간 ID 조회
        val periodId = findVatPeriodByYearAndHalf(year, halfType).id!!

        // 매입/매출 데이터 조회
        val purchaseRecords = purchaseRecordRepository
            .findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(businessIds, periodId)
        val salesRecords = salesRecordRepository
            .findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(businessIds, periodId)

        return businesses.map { business ->
            // 해당 사업장과 매칭되는 데이터 필터
            val purchases = purchaseRecords.filter { it.business.id == business.id }
            val sales = salesRecords.filter { it.business.id == business.id }

            // 매출/매입 데이터가 없으면 0 리턴
            val vat = when {
                purchases.isEmpty() && sales.isEmpty() -> 0L
                else -> calculateRoundedVat(purchases, sales)
            }

            VatDetailResDto(
                businessId = business.id!!,
                name = business.name,
                vat = vat
            )
        }
    }


    /**
     * 부가세 계산 로직
     * (매출 합계 - 매입 합계) / 11, 1의 자리 반올림
     */
    private fun calculateRoundedVat(
        purchaseRecords: List<PurchaseRecord>,
        salesRecords: List<SalesRecord>
    ): Long {
        val purchaseTotal = purchaseRecords.sumOf { it.amount }
        val salesTotal = salesRecords.sumOf { it.amount }

        val vat = (salesTotal - purchaseTotal) / rateDivisor.toDouble()
        return (vat / roundUnit.toInt()).roundToInt() * roundUnit.toLong()
    }


    // ===== Service To Service =====
    // 상/하반기에 따른 기간 ID 조회
    fun findVatPeriodByYearAndHalf(year: Int?, halfType: VatHalfType?): VatPeriod {
        val (y, h) = when {
            // year, halfType 모두 값이 있으면 사용
            year != null && halfType != null -> year to halfType

            // 만약 둘 중 하나라도 값이 없으면, 현재 기준 계산
            else -> {
                val now = LocalDate.now()
                val nowHalfType = if (now.monthValue in 1..6) FIRST else SECOND
                now.year to nowHalfType
            }
        }

        return vatPeriodRepository.findByYearAndHalf(y, h.value)
            ?: throw ApiCommonException(NOT_FOUND_VAT_PERIOD)
    }

    // 매입/매출 내역 저장
    @Transactional
    fun saveAllRecords(
        business: Business,
        request: CollectionRequest,
        readRecords: List<CollectionDataReqDto>,
        period: VatPeriod
    ) {
        val now = LocalDateTime.now()

        // purchase 매입
        purchaseRecordRepository.saveAll(
            PurchaseRecord.toEntity(
                business = business,
                request = request,
                records = readRecords.filter { it.recordType == PURCHASE },
                period = period,
                now = now
            )
        )

        // sales 매출
        salesRecordRepository.saveAll(
            SalesRecord.toEntity(
                business = business,
                request = request,
                records = readRecords.filter { it.recordType == SALES },
                period = period,
                now = now
            )
        )
    }
}