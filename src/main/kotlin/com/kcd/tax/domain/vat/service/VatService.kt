package com.kcd.tax.domain.vat.service

import com.kcd.tax.common.dto.CommonParamDto
import com.kcd.tax.common.enums.UserRole.*
import com.kcd.tax.common.error.CommonErrorCode.*
import com.kcd.tax.common.error.exception.ApiCommonException
import com.kcd.tax.domain.business.client.BusinessClient
import com.kcd.tax.domain.collection.client.CollectionClient
import com.kcd.tax.domain.collection.dto.response.RecordResDto
import com.kcd.tax.common.enums.RecordType.PURCHASE
import com.kcd.tax.common.enums.RecordType.SALES
import com.kcd.tax.domain.vat.dto.response.VatDetailResDto
import com.kcd.tax.domain.vat.dto.response.VatPeriodResDto
import com.kcd.tax.domain.vat.enums.VatHalfType
import com.kcd.tax.domain.vat.enums.VatHalfType.FIRST
import com.kcd.tax.domain.vat.enums.VatHalfType.SECOND
import com.kcd.tax.domain.vat.repository.VatPeriodRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.math.roundToInt

@Service
class VatService(
    // repository
    private val vatPeriodRepository: VatPeriodRepository,

    // external
    private val businessClient: BusinessClient,
    private val collectionClient: CollectionClient
) {

    @Value("\${vat.rate-divisor}")
    lateinit var rateDivisor: String

    @Value("\${vat.round-unit}")
    lateinit var roundUnit: String

    /**`
     * 부가세 조회
     */
    fun findVats(year: Int?, halfType: VatHalfType?, common: CommonParamDto): List<VatDetailResDto> {
        // 권한에 따른 사업장 ID 조회
        val businesses = when (common.userRole) {
            ADMIN -> businessClient.findBusinessAll()
            MANAGER -> businessClient.findBusinessByUserId(common.userId!!)
        }

        // 사업장 IDS
        val businessIds = businesses.map { it.id }

        // 조회 기간 ID 조회
        val periodId = findVatPeriodByYearAndHalf(year, halfType).id

        // 매입/매출 데이터 조회
        val records = collectionClient.findRecords(businessIds, periodId)
        val purchaseRecords = records.filter { it.recordType == PURCHASE }
        val salesRecords = records.filter { it.recordType == SALES }

        return businesses.map { business ->
            // 해당 사업장과 매칭되는 데이터 필터
            val purchases = purchaseRecords.filter { it.businessId == business.id }
            val sales = salesRecords.filter { it.businessId == business.id }

            // 매출/매입 데이터가 없으면 0 리턴
            val vat = when {
                purchases.isEmpty() && sales.isEmpty() -> 0L
                else -> calculateRoundedVat(purchases, sales)
            }

            VatDetailResDto(
                businessId = business.id,
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
        purchaseRecords: List<RecordResDto>,
        salesRecords: List<RecordResDto>
    ): Long {
        val purchaseTotal = purchaseRecords.sumOf { it.amount }
        val salesTotal = salesRecords.sumOf { it.amount }

        val vat = (salesTotal - purchaseTotal) / rateDivisor.toDouble()
        return (vat / roundUnit.toInt()).roundToInt() * roundUnit.toLong()
    }


    // ===== external =====
    // 상/하반기에 따른 기간 ID 조회
    fun findVatPeriodByYearAndHalf(year: Int?, halfType: VatHalfType?): VatPeriodResDto {
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

        val period = vatPeriodRepository.findByYearAndHalf(y, h.value)
            ?: throw ApiCommonException(NOT_FOUND_VAT_PERIOD)

        return VatPeriodResDto(period.id!!)
    }
}