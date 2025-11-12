package com.kcd.tax.infrastructure.persistence.repository

import com.kcd.tax.domain.collection.entity.SalesRecord
import org.springframework.data.jpa.repository.JpaRepository

interface SalesRecordJpaRepository : JpaRepository<SalesRecord, Long> {

    fun findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(
        businessIds: List<Long>,
        vatPeriodId: Long
    ): List<SalesRecord>
}
