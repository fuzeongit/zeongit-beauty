package com.junjie.account.core.resolver

import com.junjie.core.annotations.CurrentUserInfoId
import org.springframework.core.MethodParameter
import org.springframework.util.StringUtils
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * @author fjj
 * 获取请求的userId的解释器
 */
class CurrentUserInfoIdMethodArgumentResolver : HandlerMethodArgumentResolver {
    /**
     * 有CurrentAccountId注解的进入解释
     */
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType.isAssignableFrom(Int::class.java) && parameter.hasParameterAnnotation(CurrentUserInfoId::class.java)
    }

    /**
     * 在webRequest获取属性userId
     */
    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Int? {
        if (StringUtils.isEmpty(webRequest.getAttribute("user_info_id", RequestAttributes.SCOPE_REQUEST))) {
            return null
        }
        return webRequest.getAttribute("user_info_id", RequestAttributes.SCOPE_REQUEST) as Int
    }
}