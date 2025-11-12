package com.kcd.tax.presentation.business

import com.kcd.tax.common.dto.CommonParamDto
import com.kcd.tax.common.dto.CommonResDto
import com.kcd.tax.common.dto.ResponseDto
import com.kcd.tax.domain.business.dto.request.BusinessAuthorityInsertReqDto
import com.kcd.tax.domain.business.dto.request.BusinessAuthorityUpdateReqDto
import com.kcd.tax.domain.business.dto.response.BusinessAuthorityResDto
import com.kcd.tax.domain.business.service.BusinessService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/businesses")
class BusinessController(
    private val businessService: BusinessService
) {

    /**`
     * 사업장 권한 유저 조회
     */
    @GetMapping("/{businessId}/authorities")
    fun findBusinessAuthorities(
        @PathVariable businessId: Long,
        common: CommonParamDto
    ): ResponseDto<List<BusinessAuthorityResDto>> {
        val result = businessService.findBusinessAuthorities(businessId)
        return ResponseDto.of(result, common)
    }

    /**`
     * 새로운 유저 권한 추가
     */
    @PostMapping("/{businessId}/authorities")
    fun setBusinessAuthority(
        @PathVariable businessId: Long,
        @RequestBody dto: BusinessAuthorityInsertReqDto,
        common: CommonParamDto
    ): ResponseDto<CommonResDto> {
        businessService.setBusinessAuthority(businessId, dto)
        return ResponseDto.of(CommonResDto.success(), common)
    }

    /**`
     * 유저 권한 활성화 / 비활성화
     */
    @PutMapping("/{businessId}/authorities/{userId}")
    fun updateBusinessAuthority(
        @PathVariable businessId: Long,
        @PathVariable userId: Long,
        @RequestBody dto: BusinessAuthorityUpdateReqDto,
        common: CommonParamDto
    ): ResponseDto<CommonResDto> {
        businessService.updateBusinessAuthority(businessId, userId, dto)
        return ResponseDto.of(CommonResDto.success(), common)
    }

    /**`
     * 유저 권한 삭제
     */
    @DeleteMapping("/{businessId}/authorities/{userId}")
    fun deleteBusinessAuthority(
        @PathVariable businessId: Long,
        @PathVariable userId: Long,
        common: CommonParamDto
    ): ResponseDto<CommonResDto> {
        businessService.deleteBusinessAuthority(businessId, userId)
        return ResponseDto.of(CommonResDto.success(), common)
    }
}