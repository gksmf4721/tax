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
     * @Transactional(propagation = Propagation.REQUIRES_NEW)
     * : 스프링에서 @Async나 @Transactional 같은 기능은 실제 객체 대신 삽입된 프록시 객체를 통해서만 작동하며,
     *      같은 클래스 내에서 직접 호출하면 프록시가 우회되어 기능이 적용되지 않는 현상(자가 호출 문제)이 발생.
     *
     * 1. 수집기가 COLLECTING 상태로 업데이트 하고, 다음 작업을 실행하다가 에러가 나도,
     * 바뀐 상태 값은 유지가 되어야 하기 때문에, 기존 트랜잭션의 영향을 받지 않기 위함.
     *
     * 2. 또한, 트랜잭션이 끝나야 변경점들이 적용되기 때문에,
     * 트랜잭션의 영향을 받지 않고 상태 값을 업데이트 해줘야 하기 때문.
     * */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateCollectionRequestStatus(requestId: Long, status: CollectionStatus) {
        val now = LocalDateTime.now()

        // Row was updated or deleted by another transaction 에러 방지
        // 동일한 엔티티를 한 트랜잭션에서 수정하면 에러.
        val request = collectionRequestRepository.findById(requestId)
            .orElseThrow { throw ApiCommonException(NOT_FOUND_COLLECTION_REQUEST) }
        request.status = status.value
        request.requestedAt = now
        collectionRequestRepository.save(request)
    }
}