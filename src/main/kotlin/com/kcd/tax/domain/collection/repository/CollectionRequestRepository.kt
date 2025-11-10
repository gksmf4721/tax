package com.kcd.tax.domain.collection.repository

import com.kcd.tax.domain.collection.entity.CollectionRequest
import org.springframework.data.jpa.repository.JpaRepository

interface CollectionRequestRepository : JpaRepository<CollectionRequest, Long> {

    fun findByBusinessIdAndIsDelete(businessId: Long, isDelete: Boolean): CollectionRequest?

    fun findByBusinessIdAndVatPeriodIdAndIsDelete(businessId: Long, vatPeriodId: Long, isDelete: Boolean): CollectionRequest?
}