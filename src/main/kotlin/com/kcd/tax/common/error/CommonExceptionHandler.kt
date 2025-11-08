package com.kcd.tax.common.error

import com.kcd.tax.common.error.dto.ErrorResDto
import com.kcd.tax.common.error.exception.ApiCommonException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CommonExceptionHandler {

    private val log = LoggerFactory.getLogger(CommonExceptionHandler::class.java)

    // ApiCommonException 핸들러
    @ExceptionHandler(ApiCommonException::class)
    fun handleApiCommonException(
        e: ApiCommonException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResDto> {
        log.error("${e.code} 발생: ${e.message}", e)
        return ErrorResDto.makeErrorResponse(e, request)
    }
}