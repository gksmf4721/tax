package com.kcd.tax.domain.collection.entity

import com.kcd.tax.domain.collection.dto.request.CollectionDataReqDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "purchase_record")
data class PurchaseRecord(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_request_id", nullable = false)
    val collectionRequest: CollectionRequest,

    @Column(nullable = false)
    val vatPeriodId: Long,

    @Column(nullable = false)
    val businessId: Long,

    @Column(nullable = false)
    val amount: Long,

    @Column(nullable = false)
    val recordDate: LocalDate,

    @Column(nullable = false)
    val createdAt: LocalDateTime
)  {
    companion object {
        fun toEntity(
            businessId: Long,
            request: CollectionRequest,
            records: List<CollectionDataReqDto>,
            vatPeriodId: Long,
            now: LocalDateTime
        ): List<PurchaseRecord> {
            return records.map { record ->
                PurchaseRecord(
                    businessId = businessId,
                    collectionRequest = request,
                    vatPeriodId = vatPeriodId,
                    amount = record.amount,
                    recordDate = record.recordDate,
                    createdAt = now
                )
            }
        }
    }
}

