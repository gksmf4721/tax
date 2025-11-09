package com.kcd.tax.domain.user.service

import com.kcd.tax.common.error.CommonErrorCode.*
import com.kcd.tax.common.error.exception.ApiCommonException
import com.kcd.tax.domain.user.entity.User
import com.kcd.tax.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    // ===== Service To Service =====
    // 유저 정보 조회
    fun findUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { throw ApiCommonException(NOT_FOUND_USER) }
    }
}