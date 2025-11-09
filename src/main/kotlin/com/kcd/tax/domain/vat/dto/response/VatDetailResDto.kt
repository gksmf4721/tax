package com.kcd.tax.domain.vat.dto.response

data class VatDetailResDto(
    val businessId: Long,
    val name: String,
    val vat: Long
)