package com.kcd.tax.infrastructure.persistence.impl

import com.kcd.tax.domain.collection.entity.SalesRecord
import com.kcd.tax.domain.collection.repository.SalesRecordRepository
import com.kcd.tax.infrastructure.persistence.repository.SalesRecordJpaRepository
import org.springframework.stereotype.Repository

@Repository
class SalesRecordRepositoryImpl(
    private val salesRecordJpaRepository: SalesRecordJpaRepository
) : SalesRecordRepository {

    override fun findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(businessIds: List<Long>, periodId: Long): List<SalesRecord> {
        return salesRecordJpaRepository.findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(
            businessIds, periodId
        )
    }

    override fun saveAll(salesRecords: Iterable<SalesRecord>): List<SalesRecord> {
        return salesRecordJpaRepository.saveAll(salesRecords)
    }
}