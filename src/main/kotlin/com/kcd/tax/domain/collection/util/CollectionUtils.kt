package com.kcd.tax.domain.collection.util

import com.kcd.tax.domain.collection.dto.request.CollectionDataReqDto
import com.kcd.tax.domain.collection.enums.RecordType.PURCHASE
import com.kcd.tax.domain.collection.enums.RecordType.SALES
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.time.ZoneId

object CollectionUtils {

    /**
     * sample.xlsx 파일을 읽어서 ExcelRecord 리스트로 반환
     * 시트 이름에 따라 SALES / PURCHASE 구분
     */
    fun getCollection(): List<CollectionDataReqDto> {
        val records = mutableListOf<CollectionDataReqDto>()

        // 프로젝트 루트 기준 경로
        val filePath = "src/main/kotlin/com/kcd/tax/infrastructure/sample.xlsx"
        val file = File(filePath)
        if (!file.exists()) throw IllegalStateException("파일을 찾을 수 없습니다: $filePath")

        WorkbookFactory.create(file).use { workbook ->
            for (sheetIndex in 0 until workbook.numberOfSheets) {
                val sheet = workbook.getSheetAt(sheetIndex)
                val sheetName = sheet.sheetName.uppercase()

                val recordType = when {
                    sheetName.contains("SALES") || sheetName.contains("매출") -> SALES
                    sheetName.contains("PURCHASE") || sheetName.contains("매입") -> PURCHASE
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