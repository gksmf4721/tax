package com.kcd.tax.domain.business.dto.response

import com.kcd.tax.common.enums.UserRole

data class BusinessAuthorityResDto(
    val id: Long,
    val username: String,
    val role: UserRole,
    val isActive: Boolean
) {

    companion object {
        fun toDto(userId: Long, username: String, role: UserRole, isActive: Boolean): BusinessAuthorityResDto {
            return BusinessAuthorityResDto(
                id = userId,
                username = username,
                role = role,
                isActive = isActive
            )
        }
    }
}