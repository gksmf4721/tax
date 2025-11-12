package com.kcd.tax.infrastructure.persistence.impl

import com.kcd.tax.domain.user.entity.User
import com.kcd.tax.domain.user.repository.UserRepository
import com.kcd.tax.infrastructure.persistence.repository.UserJpaRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
): UserRepository {

    override fun findByIdIn(ids: List<Long>): List<User> {
        return userJpaRepository.findByIdIn(ids)
    }
}