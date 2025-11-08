package com.kcd.tax.common.dto

data class CommonResDto(val result: String) {

    companion object {
        fun success(): CommonResDto {
            return CommonResDto("success")
        }

        fun failure(): CommonResDto {
            return CommonResDto("failure")
        }

        fun available(): CommonResDto {
            return CommonResDto("available")
        }

        fun unavailable(): CommonResDto {
            return CommonResDto("unavailable")
        }
    }
}