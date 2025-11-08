package com.kcd.tax.common.filter

import com.kcd.tax.common.dto.CommonParamDto
import com.kcd.tax.common.enums.UserRole
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class CommonParamFilter(
    private val handlerExceptionResolver: HandlerExceptionResolver
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val userId = request.getHeader("X-User-Id")?.toLongOrNull()
            val userRole = request.getHeader("X-User-Role")?.let {
                UserRole.fromName(it)
            }

            // CommonParamDto를 request attribute에 저장
        val commonParamDto = CommonParamDto(request, userId, userRole!!)
            request.setAttribute("commonParamDto", commonParamDto)

            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            handlerExceptionResolver.resolveException(request, response, null, ex)
        }
    }
}