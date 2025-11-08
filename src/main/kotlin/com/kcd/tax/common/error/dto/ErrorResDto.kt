package com.kcd.tax.common.error.dto

import com.kcd.tax.common.error.exception.ApiCommonException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

data class ErrorResDto(
    val path: String,
    val method: String,
    val code: Int,
    val status: Int,
    val data: Any? = null,
    val message: String?,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun makeErrorResponse(
            e: ApiCommonException,
            request: HttpServletRequest,
        ): ResponseEntity<ErrorResDto> {

            val dto = ErrorResDto(
                path = request.requestURI,
                method = request.method,
                code = e.code.code,
                status = e.status.value(),
                data = e.data,
                message = e.message
            )

            return ResponseEntity
                .status(e.status)
                .body(dto)
        }
    }
}