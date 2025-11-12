package com.kcd.tax.infrastructure.adapter

import com.kcd.tax.domain.vat.client.VatClient
import com.kcd.tax.domain.vat.dto.response.VatPeriodResDto
import com.kcd.tax.domain.vat.enums.VatHalfType
import com.kcd.tax.domain.vat.service.VatService
import org.springframework.stereotype.Component

@Component
class VatClientAdapter(
    private val vatService: VatService
): VatClient {

    override fun findVatPeriodByYearAndHalf(year: Int?, halfType: VatHalfType?): VatPeriodResDto {
        return vatService.findVatPeriodByYearAndHalf(year, halfType)
    }
}