package com.kcd.tax.infrastructure.adapter

import com.kcd.tax.domain.user.client.UserClient
import com.kcd.tax.domain.user.dto.response.UserResDto
import com.kcd.tax.domain.user.service.UserService
import org.springframework.stereotype.Component

@Component
class UserClientAdapter(
    private val userService: UserService
): UserClient {

    override fun findUserIds(ids: List<Long>): List<UserResDto> {
        return userService.findUserIds(ids)
    }
}