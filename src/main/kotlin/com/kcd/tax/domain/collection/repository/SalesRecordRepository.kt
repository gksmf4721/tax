package com.kcd.tax.domain.collection.repository

import com.kcd.tax.domain.collection.entity.SalesRecord

interface SalesRecordRepository {

    fun findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(
        businessIds: List<Long>,
        periodId: Long
    ): List<SalesRecord>

    fun saveAll(salesRecords: Iterable<SalesRecord>): List<SalesRecord>
}