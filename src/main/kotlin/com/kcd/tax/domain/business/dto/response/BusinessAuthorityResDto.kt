package com.kcd.tax.domain.business.dto.response

import com.kcd.tax.common.enums.UserRole
import com.kcd.tax.domain.business.entity.BusinessAuthority

data class BusinessAuthorityResDto(
    val id: Long,
    val username: String,
    val role: UserRole,
    val isActive: Boolean
) {
    companion object {
        fun toDto(entities: List<BusinessAuthority>): List<BusinessAuthorityResDto> {
            return entities.map { entity ->
                val user = entity.user
                BusinessAuthorityResDto(
                    id = user.id!!,
                    username = user.username,
                    role = UserRole.fromValue(user.role),
                    isActive = entity.isActive
                )
            }
        }
    }
}