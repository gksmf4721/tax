package com.kcd.tax.domain.business.client

import com.kcd.tax.domain.business.dto.response.BusinessResDto

interface BusinessClient {

    // ADMIN: 모든 사업장 조회
    fun findBusinessAll(): List<BusinessResDto>

    // MANAGER: 권한 부여받은 모든 사업장 조회
    fun findBusinessByUserId(userId: Long): List<BusinessResDto>

    // 사업자 번호로 사업장 조회
    fun findBusinessByRegistrationNumber(registrationNumber: String): BusinessResDto
}