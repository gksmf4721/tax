package com.kcd.tax.domain.vat.repository

import com.kcd.tax.domain.vat.entity.SalesRecord
import org.springframework.data.jpa.repository.JpaRepository

interface SalesRecordRepository : JpaRepository<SalesRecord, Long> {

    fun findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(
        businessIds: List<Long>,
        vatPeriodId: Long
    ): List<SalesRecord>
}
