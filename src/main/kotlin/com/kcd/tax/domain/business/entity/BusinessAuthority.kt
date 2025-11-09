package com.kcd.tax.domain.business.entity

import com.kcd.tax.domain.user.entity.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

@Entity
@Table(name = "business_authority", uniqueConstraints = [UniqueConstraint(columnNames = ["business_id", "user_id"])])
data class BusinessAuthority(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    val business: Business,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    var isActive: Boolean,

    @Column(nullable = false)
    var isDelete: Boolean,

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    companion object {
        fun toEntity(business: Business, user: User, now: LocalDateTime): BusinessAuthority {
            return BusinessAuthority(
                business = business,
                user = user,
                isActive = true,
                isDelete = false,
                createdAt = now
            )
        }
    }
}