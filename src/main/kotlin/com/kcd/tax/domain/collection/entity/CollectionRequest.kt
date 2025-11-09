package com.kcd.tax.domain.collection.entity

import com.kcd.tax.domain.business.entity.Business
import com.kcd.tax.domain.vat.entity.VatPeriod
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "collection_request")
data class CollectionRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_period_id", nullable = false)
    val vatPeriod: VatPeriod,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    val business: Business,

    @Column(nullable = false)
    var requestedAt: LocalDateTime,

    @Column(nullable = false)
    var status: Int,

    @Column(nullable = false)
    var isDelete: Boolean
) {
    companion object {
        fun toEntity(
            vatPeriod: VatPeriod,
            business: Business,
            requestedAt: LocalDateTime,
            state: Int
        ): CollectionRequest {
            return CollectionRequest(
                vatPeriod = vatPeriod,
                business = business,
                requestedAt = requestedAt,
                status = state,
                isDelete = false
            )
        }
    }
}