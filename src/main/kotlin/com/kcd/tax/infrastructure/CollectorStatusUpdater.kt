package com.kcd.tax.infrastructure

import com.kcd.tax.common.error.CommonErrorCode.NOT_FOUND_COLLECTION_REQUEST
import com.kcd.tax.common.error.exception.ApiCommonException
import com.kcd.tax.domain.collection.enums.CollectionStatus
import com.kcd.tax.domain.collection.repository.CollectionRequestRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class CollectorStatusUpdater(
    private val collectionRequestRepository: CollectionRequestRepository
) {

    /**`
     * 수집 요청 상태 업데이트
     * */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateCollectionRequestStatus(requestId: Long, status: CollectionStatus) {
        val now = LocalDateTime.now()
        val request = collectionRequestRepository.findById(requestId)
            .orElseThrow { throw ApiCommonException(NOT_FOUND_COLLECTION_REQUEST) }

        request.status = status.value
        request.requestedAt = now
        collectionRequestRepository.save(request)
    }
}