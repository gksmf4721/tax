package com.kcd.tax.infrastructure.persistence.impl

import com.kcd.tax.domain.vat.entity.VatPeriod
import com.kcd.tax.domain.vat.repository.VatPeriodRepository
import com.kcd.tax.infrastructure.persistence.repository.VatPeriodJpaRepository
import org.springframework.stereotype.Repository

@Repository
class VatPeriodRepositoryImpl(
    private val vatPeriodJpaRepository: VatPeriodJpaRepository
) : VatPeriodRepository {

    // 연도와 반기 타입으로 조회
    override fun findByYearAndHalf(year: Int, half: Int): VatPeriod? {
        return vatPeriodJpaRepository.findByYearAndHalf(year, half)
    }
}