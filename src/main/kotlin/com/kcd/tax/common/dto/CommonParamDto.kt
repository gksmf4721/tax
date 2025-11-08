package com.kcd.tax.common.dto

import com.kcd.tax.common.enums.UserRole
import jakarta.servlet.http.HttpServletRequest

data class CommonParamDto(
    val request: HttpServletRequest? = null,
    val userId: Long? = null,
    val userRole: UserRole
)