package com.kcd.tax.presentation.collection

import com.kcd.tax.common.dto.CommonParamDto
import com.kcd.tax.common.dto.CommonResDto
import com.kcd.tax.common.dto.ResponseDto
import com.kcd.tax.domain.collection.dto.request.CollectionRequestReqDto
import com.kcd.tax.domain.collection.dto.response.CollectionStatusResDto
import com.kcd.tax.domain.collection.service.CollectionService
import com.kcd.tax.domain.vat.enums.VatHalfType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/businesses")
class CollectionController(
    private val collectionService: CollectionService
) {

    /**`
     * 매출/매입 수집 요청
     */
    @PostMapping("/{registrationNumber}/collections")
    fun setCollectionRequest(
        @PathVariable registrationNumber: String,
        @RequestBody dto: CollectionRequestReqDto,
        common: CommonParamDto
    ): ResponseDto<CommonResDto> {
        collectionService.setCollectionRequest(registrationNumber, dto)
        return ResponseDto.of(CommonResDto.success(), common)
    }

    /**`
     * 매출/매입 수집 상태 조회
     */
    @GetMapping("/{registrationNumber}/collections")
    fun findCollectionStatus(
        @PathVariable registrationNumber: String,
        @RequestParam year: Int?,
        @RequestParam halfType: VatHalfType?,
        common: CommonParamDto
    ): ResponseDto<CollectionStatusResDto> {
        val result = collectionService.findCollectionStatus(registrationNumber, year, halfType)
        return ResponseDto.of(result, common)
    }
}