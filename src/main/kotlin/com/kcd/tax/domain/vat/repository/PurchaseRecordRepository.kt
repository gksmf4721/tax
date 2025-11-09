package com.kcd.tax.domain.vat.repository

import com.kcd.tax.domain.vat.entity.PurchaseRecord
import org.springframework.data.jpa.repository.JpaRepository

interface PurchaseRecordRepository : JpaRepository<PurchaseRecord, Long> {

    fun findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(
        businessIds: List<Long>,
        vatPeriodId: Long
    ): List<PurchaseRecord>
}
