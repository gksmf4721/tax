package com.kcd.tax.common.error

import org.springframework.http.HttpStatus

enum class CommonErrorCode(
    val code: Int,
    val message: String,
    val status: HttpStatus
) {

    // common
    UNKNOWN_INTERNAL_ERROR(10000, "알 수 없는 서버 에러.", HttpStatus.INTERNAL_SERVER_ERROR)
}