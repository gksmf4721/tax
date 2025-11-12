package com.kcd.tax.infrastructure.persistence.impl

import com.kcd.tax.domain.business.entity.Business
import com.kcd.tax.domain.business.repository.BusinessRepository
import com.kcd.tax.infrastructure.persistence.repository.BusinessJpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class BusinessRepositoryImpl(
    private val businessJpaRepository: BusinessJpaRepository
) : BusinessRepository { // Domain BusinessRepository 인터페이스 구현

    // registrationNumber로 Business를 조회 (Domain Port 구현)
    override fun findByRegistrationNumber(registrationNumber: String): Business? {
        return businessJpaRepository.findByRegistrationNumber(registrationNumber)
    }

    // ID 리스트로 Business들을 조회 (Domain Port 구현)
    override fun findByIdIn(ids: List<Long>): List<Business> {
        return businessJpaRepository.findByIdIn(ids)
    }

    // 모든 Business를 조회 (Domain Port 구현)
    override fun findAll(): List<Business> {
        return businessJpaRepository.findAll()
    }

    // ID로 Business를 조회 (Optional 반환) (Domain Port 구현)
    override fun findById(businessId: Long): Optional<Business> {
        return businessJpaRepository.findById(businessId)
    }
}