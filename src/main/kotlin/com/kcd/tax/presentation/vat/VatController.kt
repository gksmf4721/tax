package com.kcd.tax.presentation.vat

import com.kcd.tax.common.dto.CommonParamDto
import com.kcd.tax.common.dto.ResponseDto
import com.kcd.tax.domain.vat.dto.response.VatDetailResDto
import com.kcd.tax.domain.vat.enums.VatHalfType
import com.kcd.tax.domain.vat.service.VatService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/vats")
class VatController(
    private val vatService: VatService
) {

    /**`
     * 부가세 조회
     */
    @GetMapping("")
    fun findVats(
        @RequestParam year: Int?,
        @RequestParam halfType: VatHalfType?,
        common: CommonParamDto
    ): ResponseDto<List<VatDetailResDto>> {
        val result = vatService.findVats(year, halfType, common)
        return ResponseDto.of(result, common)
    }
}