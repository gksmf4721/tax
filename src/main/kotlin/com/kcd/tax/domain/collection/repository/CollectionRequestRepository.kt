package com.kcd.tax.domain.collection.repository

import com.kcd.tax.domain.collection.entity.CollectionRequest
import java.util.Optional

interface CollectionRequestRepository {

    fun findByBusinessIdAndVatPeriodIdAndIsDelete(businessId: Long, periodId: Long, isDelete: Boolean): CollectionRequest?

    fun save(request: CollectionRequest): CollectionRequest

    fun findById(requestId: Long): Optional<CollectionRequest> // CollectorStatusUpdater에서 사용
}