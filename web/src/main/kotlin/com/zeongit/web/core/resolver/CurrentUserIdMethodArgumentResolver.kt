package com.zeongit.web.core.resolver

import com.zeongit.share.annotations.CurrentUserId
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
class CurrentUserIdMethodArgumentResolver : HandlerMethodArgumentResolver {
    /**
     * 有CurrentAccountId注解的进入解释
     */
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val parameterType = parameter.parameterType
        return (parameterType.isAssignableFrom(Integer::class.java) || parameterType.isAssignableFrom(Int::class.java))
                && parameter.hasParameterAnnotation(CurrentUserId::class.java)
    }

    /**
     * 在webRequest获取属性userId
     */
    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Int? {
        if (StringUtils.isEmpty(webRequest.getAttribute("user_id", RequestAttributes.SCOPE_REQUEST))) {
            return null
        }
        return webRequest.getAttribute("user_id", RequestAttributes.SCOPE_REQUEST) as Int
    }
}