package com.kcd.tax.domain.business.dto.response

import com.kcd.tax.domain.business.entity.Business
import java.time.LocalDateTime

data class BusinessResDto(
    val id: Long,
    val name: String,
    val registrationNumber: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun toDto(businesses: List<Business>): List<BusinessResDto> {
            return businesses.map { business ->
                BusinessResDto(
                    id = business.id!!,
                    name = business.name,
                    registrationNumber = business.registrationNumber,
                    createdAt = business.createdAt,
                    updatedAt = business.updatedAt
                )
            }
        }

        fun toDto(business: Business): BusinessResDto {
            return BusinessResDto(
                id = business.id!!,
                name = business.name,
                registrationNumber = business.registrationNumber,
                createdAt = business.createdAt,
                updatedAt = business.updatedAt
            )
        }
    }
}