package com.kcd.tax.domain.collection.dto.response

import com.kcd.tax.domain.collection.entity.PurchaseRecord
import com.kcd.tax.domain.collection.entity.SalesRecord
import com.kcd.tax.common.enums.RecordType

data class RecordResDto(
    val id: Long,
    val businessId: Long,
    val amount: Long,
    val recordType: RecordType
) {
    companion object {
        fun fromPurchase(records: List<PurchaseRecord>): List<RecordResDto> {
            return records.map { record ->
                RecordResDto(
                    id = record.id!!,
                    businessId = record.businessId,
                    amount = record.amount,
                    recordType = RecordType.PURCHASE
                )
            }
        }

        fun fromSales(records: List<SalesRecord>): List<RecordResDto> {
            return records.map { record ->
                RecordResDto(
                    id = record.id!!,
                    businessId = record.businessId,
                    amount = record.amount,
                    recordType = RecordType.SALES
                )
            }
        }
    }
}