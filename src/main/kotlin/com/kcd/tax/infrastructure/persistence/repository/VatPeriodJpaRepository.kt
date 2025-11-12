package com.kcd.tax.infrastructure.persistence.repository

import com.kcd.tax.domain.vat.entity.VatPeriod
import org.springframework.data.jpa.repository.JpaRepository

interface VatPeriodJpaRepository : JpaRepository<VatPeriod, Long> {

    fun findByYearAndHalf(year: Int, half: Int): VatPeriod?
}