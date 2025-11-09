package com.kcd.tax.domain.vat.repository

import com.kcd.tax.domain.vat.entity.VatPeriod
import org.springframework.data.jpa.repository.JpaRepository

interface VatPeriodRepository : JpaRepository<VatPeriod, Long> {

    fun findByYearAndHalf(year: Int, half: Int): VatPeriod?
}