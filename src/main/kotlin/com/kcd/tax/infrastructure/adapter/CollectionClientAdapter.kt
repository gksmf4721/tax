package com.kcd.tax.infrastructure.adapter

import com.kcd.tax.domain.collection.client.CollectionClient
import com.kcd.tax.domain.collection.dto.request.CollectionDataReqDto
import com.kcd.tax.domain.collection.dto.response.RecordResDto
import com.kcd.tax.domain.collection.entity.CollectionRequest
import com.kcd.tax.domain.collection.service.CollectionService
import org.springframework.stereotype.Component
import org.springframework.context.annotation.Lazy

@Component
class CollectionClientAdapter(
    @Lazy private val collectionService: CollectionService
): CollectionClient {

    override fun findRecords(businessIds: List<Long>, periodId: Long): List<RecordResDto> {
        return collectionService.findRecords(businessIds, periodId)
    }

    override fun saveAllRecords(
        businessId: Long,
        request: CollectionRequest,
        readRecords: List<CollectionDataReqDto>,
        vatPeriodId: Long
    ) {
        collectionService.saveAllRecords(businessId, request, readRecords, vatPeriodId)
    }
}