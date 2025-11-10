package com.kcd.tax.domain.business.service

import com.kcd.tax.common.error.CommonErrorCode.*
import com.kcd.tax.common.error.exception.ApiCommonException
import com.kcd.tax.domain.business.dto.request.BusinessAuthorityUpdateReqDto
import com.kcd.tax.domain.business.dto.response.BusinessAuthorityResDto
import com.kcd.tax.domain.business.entity.Business
import com.kcd.tax.domain.business.entity.BusinessAuthority
import com.kcd.tax.domain.business.repository.BusinessAuthorityRepository
import com.kcd.tax.domain.business.repository.BusinessRepository
import com.kcd.tax.domain.user.service.UserService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BusinessService(
    // repository
    private val businessRepository: BusinessRepository,
    private val businessAuthorityRepository: BusinessAuthorityRepository,

    // service to service
    private val userService: UserService
) {

    /**`
     * 사업장 권한 유저 조회
     */
    fun findBusinessAuthorities(businessId: Long): List<BusinessAuthorityResDto> {
        return businessAuthorityRepository.findByBusinessIdAndIsDelete(businessId, false)
            .let { BusinessAuthorityResDto.toDto(it) }
    }

    /**`
     * 새로운 유저 권한 추가
     */
    fun setBusinessAuthority(businessId: Long, userId: Long){
        val now = LocalDateTime.now()

        val user = userService.findUserById(userId)
        val business = businessRepository.findById(businessId)
            .orElseThrow { throw ApiCommonException(NOT_FOUND_BUSINESS) }

        businessAuthorityRepository.save(
            BusinessAuthority.toEntity(business, user, now)
        )
    }

    /**`
     * 유저 권한 활성화 / 비활성화
     */
    fun updateBusinessAuthority(businessId: Long, userId: Long, dto: BusinessAuthorityUpdateReqDto) {
        val authority = businessAuthorityRepository.findByUserIdAndBusinessIdAndIsDelete(
            userId, businessId, false
        )
        authority.isActive = dto.isActive
        businessAuthorityRepository.save(authority)
    }

    /**`
     * 유저 권한 삭제
     */
    fun deleteBusinessAuthority(businessId: Long, userId: Long) {
        val isDelete = true
        val authority = businessAuthorityRepository.findByUserIdAndBusinessIdAndIsDelete(
            userId, businessId, !isDelete
        )
        authority.isDelete = isDelete
        businessAuthorityRepository.save(authority)
    }


    // ===== Service To Service =====
    // ADMIN: 모든 사업장 조회
    fun findBusinessAll(): List<Business> {
        return businessRepository.findAll()
    }

    // MANAGER: 권한 부여받은 모든 사업장 조회
    fun findBusinessByUserId(userId: Long): List<Business> {
        return businessAuthorityRepository.findByUserIdAndIsActiveAndIsDelete(userId, true, false)
            .map { it.business }
    }

    // 사업자 번호로 사업장 조회
    fun findBusinessByRegistrationNumber(registrationNumber: String): Business {
        return businessRepository.findByRegistrationNumber(registrationNumber)
            ?: throw ApiCommonException(NOT_FOUND_BUSINESS)
    }
}