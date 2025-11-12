package com.kcd.tax.domain.user.dto.response

import com.kcd.tax.common.enums.UserRole
import com.kcd.tax.domain.user.entity.User

data class UserResDto(
    val id: Long,
    val username: String,
    val role: UserRole
) {
    companion object {
        fun toDto(users: List<User>): List<UserResDto> {
            return users.map { user ->
                UserResDto(
                    id = user.id!!,
                    username = user.username,
                    role = UserRole.fromValue(user.role)
                )
            }
        }
    }
}