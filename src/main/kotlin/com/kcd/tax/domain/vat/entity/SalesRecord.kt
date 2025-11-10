package com.kcd.tax.domain.vat.entity

import com.kcd.tax.domain.business.entity.Business
import com.kcd.tax.domain.collection.dto.request.CollectionDataReqDto
import com.kcd.tax.domain.collection.entity.CollectionRequest
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
@Table(name = "sales_record")
data class SalesRecord(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_period_id", nullable = false)
    val vatPeriod: VatPeriod,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    val business: Business,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_request_id", nullable = false)
    val collectionRequest: CollectionRequest,

    @Column(nullable = false)
    val amount: Long,

    @Column(nullable = false)
    val recordDate: LocalDate,

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    companion object {
        fun toEntity(
            business: Business,
            request: CollectionRequest,
            records: List<CollectionDataReqDto>,
            period: VatPeriod,
            now: LocalDateTime
        ): List<SalesRecord> {
            return records.map { record ->
                SalesRecord(
                    business = business,
                    collectionRequest = request,
                    vatPeriod = period,
                    amount = record.amount,
                    recordDate = record.recordDate,
                    createdAt = now
                )
            }
        }
    }
}
