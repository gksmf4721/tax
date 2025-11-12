package com.kcd.tax.domain.collection.repository

import com.kcd.tax.domain.collection.entity.PurchaseRecord

interface PurchaseRecordRepository {

    fun findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(
        businessIds: List<Long>,
        periodId: Long
    ): List<PurchaseRecord>

    fun saveAll(purchaseRecords: Iterable<PurchaseRecord>): List<PurchaseRecord>
}