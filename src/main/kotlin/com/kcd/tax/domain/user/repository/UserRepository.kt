package com.kcd.tax.domain.user.repository

import com.kcd.tax.domain.user.entity.User

interface UserRepository {

    fun findByIdIn(ids: List<Long>): List<User>
}