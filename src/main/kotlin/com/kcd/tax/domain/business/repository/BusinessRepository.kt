package com.kcd.tax.domain.business.repository

import com.kcd.tax.domain.business.entity.Business
import org.springframework.data.jpa.repository.JpaRepository

interface BusinessRepository : JpaRepository<Business, Long> {

    fun findByRegistrationNumber(registrationNumber: String): Business?
}