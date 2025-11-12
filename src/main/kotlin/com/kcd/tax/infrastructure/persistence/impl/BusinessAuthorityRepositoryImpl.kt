package com.kcd.tax.infrastructure.persistence.impl

import com.kcd.tax.domain.business.entity.BusinessAuthority
import com.kcd.tax.domain.business.repository.BusinessAuthorityRepository
import com.kcd.tax.infrastructure.persistence.repository.BusinessAuthorityJpaRepository
import org.springframework.stereotype.Repository

@Repository
class BusinessAuthorityRepositoryImpl(
    private val businessAuthorityJpaRepository: BusinessAuthorityJpaRepository
) : BusinessAuthorityRepository {

    // BusinessId와 삭제 여부로 조회
    override fun findByBusinessIdAndIsDelete(businessId: Long, isDelete: Boolean): List<BusinessAuthority> {
        return businessAuthorityJpaRepository.findByBusinessIdAndIsDelete(businessId, isDelete)
    }

    // UserId, 활성화 여부, 삭제 여부로 조회
    override fun findByUserIdAndIsActiveAndIsDelete(userId: Long, isActive: Boolean, isDelete: Boolean): List<BusinessAuthority> {
        return businessAuthorityJpaRepository.findByUserIdAndIsActiveAndIsDelete(userId, isActive, isDelete)
    }

    // UserId, BusinessId, 삭제 여부로 단건 조회
    override fun findByUserIdAndBusinessIdAndIsDelete(userId: Long, businessId: Long, isDelete: Boolean): BusinessAuthority {
        return businessAuthorityJpaRepository.findByUserIdAndBusinessIdAndIsDelete(userId, businessId, isDelete)
    }

    // 저장
    override fun save(businessAuthority: BusinessAuthority) {
        businessAuthorityJpaRepository.save(businessAuthority)
    }
}