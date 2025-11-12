package com.kcd.tax.domain.business.entity

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
@Table(name = "business_authority")
data class BusinessAuthority(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    val business: Business,

    @Column(nullable = false)
    val userId: Long,

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    var isActive: Boolean,

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    var isDelete: Boolean,

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    companion object {
        fun toEntity(business: Business, userId: Long, now: LocalDateTime): BusinessAuthority {
            return BusinessAuthority(
                business = business,
                userId = userId,
                isActive = true,
                isDelete = false,
                createdAt = now
            )
        }
    }
}