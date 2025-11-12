package com.kcd.tax.domain.user.client

import com.kcd.tax.domain.user.dto.response.UserResDto

interface UserClient {

    // 유저 정보 조회
    fun findUserIds(ids: List<Long>): List<UserResDto>
}