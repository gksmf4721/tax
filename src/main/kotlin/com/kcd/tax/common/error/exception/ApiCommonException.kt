package com.kcd.tax.common.error.exception

import com.kcd.tax.common.error.CommonErrorCode
import com.kcd.tax.common.error.CommonErrorCode.*
import org.springframework.http.HttpStatus

open class ApiCommonException : RuntimeException {

    override var cause = super.cause
    var code: CommonErrorCode = UNKNOWN_INTERNAL_ERROR
    var status: HttpStatus = UNKNOWN_INTERNAL_ERROR.status
    final override var message: String? = UNKNOWN_INTERNAL_ERROR.message
    var data: Any? = null

    constructor()

    constructor(code: CommonErrorCode) {
        this.message = code.message
        this.code = code
        this.status = code.status
    }

    constructor(code: CommonErrorCode, message: String) {
        this.message = message
        this.code = code
        this.status = code.status
    }
}