package com.kcd.tax.domain.vat.client

import com.kcd.tax.domain.vat.dto.response.VatPeriodResDto
import com.kcd.tax.domain.vat.enums.VatHalfType

interface VatClient {

    // 상/하반기에 따른 기간 ID 조회
    fun findVatPeriodByYearAndHalf(year: Int?, halfType: VatHalfType?): VatPeriodResDto
}