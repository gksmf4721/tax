package com.kcd.tax.infrastructure.persistence.repository

import com.kcd.tax.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<User, Long> {

    fun findByIdIn(ids: List<Long>): List<User>
}