package com.kcd.tax.common.filter

import com.kcd.tax.common.dto.CommonParamDto
import com.kcd.tax.common.enums.UserRole
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class CommonParamArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == CommonParamDto::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
        val userIdHeader = request?.getHeader("X-User-Id")
        val userRoleHeader = request?.getHeader("X-User-Role")

        val commonParamDto = CommonParamDto(
            request = request,
            userId = userIdHeader?.toLong(),
            userRole = UserRole.fromName(userRoleHeader)
        )

        return commonParamDto
    }
}