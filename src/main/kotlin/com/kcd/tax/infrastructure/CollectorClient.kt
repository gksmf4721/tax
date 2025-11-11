package com.kcd.tax.infrastructure

import com.kcd.tax.common.error.CommonErrorCode.*
import com.kcd.tax.common.error.exception.ApiCommonException
import com.kcd.tax.domain.business.entity.Business
import com.kcd.tax.domain.collection.entity.CollectionRequest
import com.kcd.tax.domain.collection.enums.CollectionStatus.*
import com.kcd.tax.domain.collection.util.CollectionUtils
import com.kcd.tax.domain.vat.entity.VatPeriod
import com.kcd.tax.domain.vat.service.VatService
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant

@Component
class CollectorClient(
    private val vatService: VatService,
    private val collectorStatusUpdater: CollectorStatusUpdater,

    // scheduler
    private val taskScheduler: TaskScheduler
) {

    /**`
     * TaskScheduler
     * : Thread.sleep() 대신, 딜레이 후 작업 실행을 비동기적으로 처리 (스레드 점유 X)
     * */
    @Async
    fun processCollectionAsync(business: Business, request: CollectionRequest, period: VatPeriod) {
        try {
            val requestId = request.id!!

            // 즉시 COLLECTING 상태로 변경
            collectorStatusUpdater.updateCollectionRequestStatus(requestId, COLLECTING)

            // 5분 뒤 데이터 수집 로직 예약 (논블로킹)
            taskScheduler.schedule({
                val records = CollectionUtils.getCollection()
                vatService.saveAllRecords(business, request, records, period)

                // 작업이 성공적으로 수행되면 COLLECTED 상태로 변경
                collectorStatusUpdater.updateCollectionRequestStatus(requestId, COLLECTED)
            }, Instant.now().plus(Duration.ofMinutes(5)))

        } catch (e: Exception) {

            val message = e.message ?: FAIL_COLLECTION.message
            throw ApiCommonException(FAIL_COLLECTION, message)
        }
    }
}
