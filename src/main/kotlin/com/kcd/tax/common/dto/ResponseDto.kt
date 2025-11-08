package com.kcd.tax.common.dto

import java.time.LocalDateTime

data class ResponseDto<T> (
    val path: String,
    val method: String,
    val timestamp: LocalDateTime,
    val status: Int? = 200,
    val data: T
) {
    companion object {
        fun <T> of(data: T, common: CommonParamDto): ResponseDto<T> {
            return ResponseDto(
                path = common.request?.requestURL.toString(),
                method = common.request?.method.toString(),
                timestamp = LocalDateTime.now(),
                data = data
            )
        }
    }
}