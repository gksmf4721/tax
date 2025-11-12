package com.kcd.tax.domain.business.service

import com.kcd.tax.common.error.CommonErrorCode.*
import com.kcd.tax.common.error.exception.ApiCommonException
import com.kcd.tax.domain.business.dto.request.BusinessAuthorityInsertReqDto
import com.kcd.tax.domain.business.dto.request.BusinessAuthorityUpdateReqDto
import com.kcd.tax.domain.business.dto.response.BusinessAuthorityResDto
import com.kcd.tax.domain.business.dto.response.BusinessResDto
import com.kcd.tax.domain.business.entity.BusinessAuthority
import com.kcd.tax.domain.business.repository.BusinessAuthorityRepository
import com.kcd.tax.domain.business.repository.BusinessRepository
import com.kcd.tax.infrastructure.persistence.repository.BusinessAuthorityJpaRepository
import com.kcd.tax.infrastructure.persistence.repository.BusinessJpaRepository
import com.kcd.tax.domain.user.client.UserClient
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BusinessService(
    // repository
    private val businessRepository: BusinessRepository,
    private val businessAuthorityRepository: BusinessAuthorityRepository,

    // external
    private val userClient: UserClient
) {

    /**`
     * 사업장 권한 유저 조회
     */
    fun findBusinessAuthorities(businessId: Long): List<BusinessAuthorityResDto> {
        val authorities = businessAuthorityRepository.findByBusinessIdAndIsDelete(businessId, false)
        val users = userClient.findUserIds(authorities.map { it.userId })

        return authorities.map { authority ->
            users.find { it.id == authority.userId }!!.let {
                BusinessAuthorityResDto.toDto(
                    userId = it.id,
                    username = it.username,
                    role = it.role,
                    isActive = authority.isActive
                )
            }
        }
    }

    /**`
     * 새로운 유저 권한 추가
     */
    fun setBusinessAuthority(businessId: Long, dto: BusinessAuthorityInsertReqDto){
        val now = LocalDateTime.now()
        val userId = dto.userId

        val business = businessRepository.findById(businessId)
            .orElseThrow { throw ApiCommonException(NOT_FOUND_BUSINESS) }

        businessAuthorityRepository.save(
            BusinessAuthority.toEntity(business, userId, now)
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


    // ===== external =====
    // ADMIN: 모든 사업장 조회
    fun findBusinessAll(): List<BusinessResDto> {
        return businessRepository.findAll()
            .let { BusinessResDto.toDto(it) }
    }

    // MANAGER: 권한 부여받은 모든 사업장 조회
    fun findBusinessByUserId(userId: Long): List<BusinessResDto> {
        val authorities =  businessAuthorityRepository.findByUserIdAndIsActiveAndIsDelete(userId, true, false)
            .map { it.business }

        return businessRepository.findByIdIn(authorities.map { it.id!! })
            .let { BusinessResDto.toDto(it) }
    }

    // 사업자 번호로 사업장 조회
    fun findBusinessByRegistrationNumber(registrationNumber: String): BusinessResDto {
        val business = businessRepository.findByRegistrationNumber(registrationNumber)
            ?: throw ApiCommonException(NOT_FOUND_BUSINESS)

        return BusinessResDto.toDto(business)
    }
}