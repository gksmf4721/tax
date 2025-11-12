package com.kcd.tax.infrastructure.external.collector

import com.kcd.tax.common.error.exception.ApiCommonException
import com.kcd.tax.domain.collection.client.CollectionClient
import com.kcd.tax.domain.collection.dto.request.CollectionDataReqDto
import com.kcd.tax.domain.collection.entity.CollectionRequest
import com.kcd.tax.domain.collection.enums.CollectionStatus
import com.kcd.tax.common.enums.RecordType.*
import com.kcd.tax.common.error.CommonErrorCode.*
import com.kcd.tax.infrastructure.persistence.support.CollectorStatusUpdater
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

@Component
class CollectorClient(
    private val collectorStatusUpdater: CollectorStatusUpdater,

    // scheduler
    private val taskScheduler: TaskScheduler,

    // external
    private val collectionClient: CollectionClient
) {

    @Value("\${common.collector.path}")
    lateinit var collectorPath: String

    /**`
     * TaskScheduler
     * : Thread.sleep() 대신, 딜레이 후 작업 실행을 비동기적으로 처리 (스레드 점유 X)
     * */
    @Async
    fun processCollectionAsync(businessId: Long, request: CollectionRequest, vatPeriodId: Long) {
        try {
            val requestId = request.id!!

            // 즉시 COLLECTING 상태로 변경
            collectorStatusUpdater.updateCollectionRequestStatus(requestId, CollectionStatus.COLLECTING)

            // 5분 뒤 데이터 수집 로직 예약 (논블로킹)
            taskScheduler.schedule({
                val records = getCollection()
                collectionClient.saveAllRecords(businessId, request, records, vatPeriodId)

                // 작업이 성공적으로 수행되면 COLLECTED 상태로 변경
                collectorStatusUpdater.updateCollectionRequestStatus(requestId, CollectionStatus.COLLECTED)
            }, Instant.now().plus(Duration.ofMinutes(5)))

        } catch (e: Exception) {

            val message = e.message ?: FAIL_COLLECTION.message
            throw ApiCommonException(FAIL_COLLECTION, message)
        }
    }


    /**
     * sample.xlsx 파일을 읽어서 리스트로 반환
     * 시트 이름에 따라 SALES / PURCHASE 구분
     */
    private fun getCollection(): List<CollectionDataReqDto> {
        val records = mutableListOf<CollectionDataReqDto>()

        // 프로젝트 루트 기준 경로
        val file = File(collectorPath)

        WorkbookFactory.create(file).use { workbook ->
            for (sheetIndex in 0 until workbook.numberOfSheets) {
                val sheet = workbook.getSheetAt(sheetIndex)
                val sheetName = sheet.sheetName.uppercase()

                val recordType = when {
                    sheetName.contains("매출") -> SALES
                    sheetName.contains("매입") -> PURCHASE
                    else -> continue
                }

                for (rowIndex in 0..sheet.lastRowNum) {
                    val row = sheet.getRow(rowIndex) ?: continue
                    val amountCell = row.getCell(0) ?: continue
                    val dateCell = row.getCell(1) ?: continue

                    val amount = amountCell.numericCellValue.toLong()
                    val recordDate = dateCell.dateCellValue.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    records.add(CollectionDataReqDto(recordType, amount, recordDate))
                }
            }
        }

        return records
    }
}