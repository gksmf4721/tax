package com.kcd.tax.domain.business.repository

import com.kcd.tax.domain.business.entity.BusinessAuthority
import org.springframework.data.jpa.repository.JpaRepository

interface BusinessAuthorityRepository : JpaRepository<BusinessAuthority, Long> {

    fun findByBusinessIdAndIsDelete(businessId: Long, isDelete: Boolean): List<BusinessAuthority>

    fun findByUserIdAndIsActiveAndIsDelete(userId: Long, isActive: Boolean, isDelete: Boolean): List<BusinessAuthority>

    fun findByUserIdAndBusinessIdAndIsDelete(userId: Long, businessId: Long, isDelete: Boolean): BusinessAuthority
}