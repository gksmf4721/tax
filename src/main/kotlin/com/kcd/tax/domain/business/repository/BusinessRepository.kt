package com.kcd.tax.domain.business.repository

import com.kcd.tax.domain.business.entity.Business
import java.util.Optional

interface BusinessRepository {

    fun findByRegistrationNumber(registrationNumber: String): Business?

    fun findByIdIn(ids: List<Long>): List<Business>

    fun findAll(): List<Business>

    fun findById(businessId: Long): Optional<Business>
}