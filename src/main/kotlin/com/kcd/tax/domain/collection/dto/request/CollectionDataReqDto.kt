package com.kcd.tax.domain.collection.dto.request

import com.kcd.tax.common.enums.RecordType
import java.time.LocalDate

data class CollectionDataReqDto(
    val recordType: RecordType,
    val amount: Long,
    val recordDate: LocalDate
)