package com.kcd.tax.domain.collection.dto.request

import com.kcd.tax.domain.vat.enums.VatHalfType

data class CollectionRequestReqDto(
    val year: Int?,
    val halfType: VatHalfType?
)