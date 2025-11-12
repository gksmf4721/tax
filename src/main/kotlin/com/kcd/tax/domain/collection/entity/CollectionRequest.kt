package com.kcd.tax.domain.collection.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "collection_request")
data class CollectionRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val vatPeriodId: Long,

    @Column(nullable = false)
    val businessId: Long,

    @Column(nullable = false)
    var requestedAt: LocalDateTime,

    @Column(nullable = false, columnDefinition = "TINYINT UNSIGNED")
    var status: Int,

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    var isDelete: Boolean
) {
    companion object {
        fun toEntity(
            vatPeriodId: Long,
            businessId: Long,
            requestedAt: LocalDateTime,
            state: Int
        ): CollectionRequest {
            return CollectionRequest(
                vatPeriodId = vatPeriodId,
                businessId = businessId,
                requestedAt = requestedAt,
                status = state,
                isDelete = false
            )
        }
    }
}