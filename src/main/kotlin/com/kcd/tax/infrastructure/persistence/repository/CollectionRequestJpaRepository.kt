package com.kcd.tax.infrastructure.persistence.repository

import com.kcd.tax.domain.collection.entity.CollectionRequest
import org.springframework.data.jpa.repository.JpaRepository

interface CollectionRequestJpaRepository : JpaRepository<CollectionRequest, Long> {

    fun findByBusinessIdAndVatPeriodIdAndIsDelete(businessId: Long, vatPeriodId: Long, isDelete: Boolean): CollectionRequest?
}