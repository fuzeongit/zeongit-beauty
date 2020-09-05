package com.zeongit.web.core.configurer

import com.zeongit.qiniu.core.component.QiniuConfig
import com.zeongit.web.core.interceptor.AuthInterceptor
import com.zeongit.web.core.resolver.CurrentUserIdMethodArgumentResolver
import com.zeongit.web.service.UserInfoService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * @author fjj
 * 程序的配置清单
 */
@Configuration
class ProgramConfigurer() : WebMvcConfigurer {
    @Bean
    internal fun qiniuConfig(): QiniuConfig {
        return QiniuConfig()
    }

    /**
     * 拦截器
     */
    override fun addInterceptors(registry: InterceptorRegistry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(authInterceptor()).addPathPatterns("/**")
        super.addInterceptors(registry)
    }

    /**
     * 解析器
     */
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(currentUserMethodArgumentResolver())
        super.addArgumentResolvers(argumentResolvers)
    }


    @Bean
    internal fun authInterceptor(): AuthInterceptor {
        return AuthInterceptor()
    }

    @Bean
    fun currentUserMethodArgumentResolver(): CurrentUserIdMethodArgumentResolver {
        return CurrentUserIdMethodArgumentResolver()
    }
}