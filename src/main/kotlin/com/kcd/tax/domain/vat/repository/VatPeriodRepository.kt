package com.kcd.tax.domain.vat.repository

import com.kcd.tax.domain.vat.entity.VatPeriod

interface VatPeriodRepository {

    fun findByYearAndHalf(year: Int, half: Int): VatPeriod?
}