package com.kcd.tax.infrastructure.persistence.impl

import com.kcd.tax.domain.collection.entity.PurchaseRecord
import com.kcd.tax.domain.collection.repository.PurchaseRecordRepository
import com.kcd.tax.infrastructure.persistence.repository.PurchaseRecordJpaRepository
import org.springframework.stereotype.Repository

@Repository
class PurchaseRecordRepositoryImpl(
    private val purchaseRecordJpaRepository: PurchaseRecordJpaRepository
) : PurchaseRecordRepository {

    override fun findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(businessIds: List<Long>, periodId: Long): List<PurchaseRecord> {
        return purchaseRecordJpaRepository.findByBusinessIdInAndVatPeriodIdAndCollectionRequestIsDeleteFalse(
            businessIds, periodId
        )
    }

    override fun saveAll(purchaseRecords: Iterable<PurchaseRecord>): List<PurchaseRecord> {
        return purchaseRecordJpaRepository.saveAll(purchaseRecords)
    }
}