package com.kcd.tax.infrastructure.persistence.impl

import com.kcd.tax.domain.collection.entity.CollectionRequest
import com.kcd.tax.domain.collection.repository.CollectionRequestRepository
import com.kcd.tax.infrastructure.persistence.repository.CollectionRequestJpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class CollectionRequestRepositoryImpl(
    private val collectionRequestJpaRepository: CollectionRequestJpaRepository
) : CollectionRequestRepository {

    override fun findByBusinessIdAndVatPeriodIdAndIsDelete(businessId: Long, periodId: Long, isDelete: Boolean): CollectionRequest? {
        return collectionRequestJpaRepository.findByBusinessIdAndVatPeriodIdAndIsDelete(businessId, periodId, isDelete)
    }

    override fun save(request: CollectionRequest): CollectionRequest {
        return collectionRequestJpaRepository.save(request)
    }

    override fun findById(requestId: Long): Optional<CollectionRequest> {
        return collectionRequestJpaRepository.findById(requestId)
    }
}