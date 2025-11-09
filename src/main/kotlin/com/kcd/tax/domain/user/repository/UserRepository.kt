package com.kcd.tax.domain.user.repository

import com.kcd.tax.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
}