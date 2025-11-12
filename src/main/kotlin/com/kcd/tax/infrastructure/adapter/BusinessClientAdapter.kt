package com.kcd.tax.infrastructure.adapter

import com.kcd.tax.domain.business.client.BusinessClient
import com.kcd.tax.domain.business.dto.response.BusinessResDto
import com.kcd.tax.domain.business.service.BusinessService
import org.springframework.stereotype.Component

@Component
class BusinessClientAdapter(
    private val businessService: BusinessService
): BusinessClient {

    override fun findBusinessAll(): List<BusinessResDto> {
        return businessService.findBusinessAll()
    }

    override fun findBusinessByUserId(userId: Long): List<BusinessResDto> {
        return businessService.findBusinessByUserId(userId)
    }

    override fun findBusinessByRegistrationNumber(registrationNumber: String): BusinessResDto {
        return businessService.findBusinessByRegistrationNumber(registrationNumber)
    }
}