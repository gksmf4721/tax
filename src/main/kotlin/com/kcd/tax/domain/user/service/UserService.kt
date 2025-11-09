package com.kcd.tax.domain.user.service

import com.kcd.tax.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

}