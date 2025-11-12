package com.kcd.tax.infrastructure.persistence.repository

import com.kcd.tax.domain.collection.entity.PurchaseRecord
import org.springframework.data.jpa.repository.JpaRepository

interface PurchaseRecordJpaRepository : JpaRepository<PurchaseRecord, Long> {

    fun findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(
        businessIds: List<Long>,
        vatPeriodId: Long
    ): List<PurchaseRecord>
}
