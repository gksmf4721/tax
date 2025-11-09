package com.kcd.tax.domain.vat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "vat_period")
data class VatPeriod(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val year: Int,

    @Column(nullable = false)
    val half: Int,

    @Column(nullable = false)
    val createdAt: LocalDateTime
)
