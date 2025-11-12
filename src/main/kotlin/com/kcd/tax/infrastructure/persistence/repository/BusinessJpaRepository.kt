package com.kcd.tax.infrastructure.persistence.repository

import com.kcd.tax.domain.business.entity.Business
import org.springframework.data.jpa.repository.JpaRepository

interface BusinessJpaRepository : JpaRepository<Business, Long> {

    fun findByRegistrationNumber(registrationNumber: String): Business?

    fun findByIdIn(ids: List<Long>): List<Business>
}