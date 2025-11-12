package com.kcd.tax.domain.user.service

import com.kcd.tax.domain.user.dto.response.UserResDto
import com.kcd.tax.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    // ===== external =====
    // 유저 정보 조회
    fun findUserIds(ids: List<Long>): List<UserResDto>{
        val user = userRepository.findByIdIn(ids)

        return UserResDto.toDto(user)
    }
}