package com.kcd.tax.common.filter

import com.kcd.tax.common.dto.CommonParamDto
import com.kcd.tax.common.enums.UserRole
import com.kcd.tax.common.enums.UserRole.ADMIN
import com.kcd.tax.common.error.CommonErrorCode.*
import com.kcd.tax.common.error.exception.ApiCommonException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class CommonParamFilter(
    private val handlerExceptionResolver: HandlerExceptionResolver
) : OncePerRequestFilter() {

    private val pathMatcher = AntPathMatcher()
    private val restrictedEndpoints = listOf(
        "/api/businesses/*/authorities",
        "/api/businesses/*/authorities/*"
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val userId = request.getHeader("X-User-Id")?.toLongOrNull()
            val userRole = request.getHeader("X-User-Role")?.let {
                UserRole.fromName(it)
            } ?: throw ApiCommonException(NOT_FOUND_AUTHORITY)

            val requestURI = request.requestURI

            // ADMIN이 아닌 경우 특정 경로 접근 차단
            if (restrictedEndpoints.any { pathMatcher.match(it, requestURI) } && userRole != ADMIN) {
                throw ApiCommonException(NOT_HAVE_AUTHORITY)
            }

            // CommonParamDto를 request attribute에 저장
            val commonParamDto = CommonParamDto(request, userId, userRole)
            request.setAttribute("commonParamDto", commonParamDto)

            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            handlerExceptionResolver.resolveException(request, response, null, ex)
        }
    }
}