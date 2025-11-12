package com.kcd.tax.domain.collection.client

import com.kcd.tax.domain.collection.dto.request.CollectionDataReqDto
import com.kcd.tax.domain.collection.dto.response.RecordResDto
import com.kcd.tax.domain.collection.entity.CollectionRequest

interface CollectionClient {

    // 매출/매입 내역 조회
    fun findRecords(businessIds: List<Long>, periodId: Long): List<RecordResDto>

    // 매출/매입 내역 저장
    fun saveAllRecords(
        businessId: Long,
        request: CollectionRequest,
        readRecords: List<CollectionDataReqDto>,
        vatPeriodId: Long
    )
}