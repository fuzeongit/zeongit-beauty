package com.junjie.account.core.configurer


import com.junjie.account.component.AccountConfig
import com.junjie.account.core.interceptor.AuthInterceptor
import com.junjie.account.core.resolver.CurrentUserIdMethodArgumentResolver
import com.junjie.account.core.resolver.CurrentUserInfoIdMethodArgumentResolver
import com.junjie.account.service.UserService
import com.junjie.account.service.UserInfoService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author fjj
 * 程序的配置清单
 */
@Configuration
class AccountConfigurer(
        private val accountService: UserService,
        private val userService: UserInfoService
) : WebMvcConfigurer {
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
        argumentResolvers.add(currentUserInfoIdMethodArgumentResolver())
        super.addArgumentResolvers(argumentResolvers)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨域请求的域名
                .allowedOrigins("*")
                //是否允许证书 不再默认开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*")
                //跨域允许时间
                .maxAge(3600)
    }

    @Bean
    internal fun authInterceptor(): AuthInterceptor {
        return AuthInterceptor(accountConfig(), accountService, userService)
    }

    @Bean
    fun currentUserMethodArgumentResolver(): CurrentUserIdMethodArgumentResolver {
        return CurrentUserIdMethodArgumentResolver()
    }

    @Bean
    fun currentUserInfoIdMethodArgumentResolver(): CurrentUserInfoIdMethodArgumentResolver {
        return CurrentUserInfoIdMethodArgumentResolver()
    }

    @Bean
    internal fun accountConfig(): AccountConfig {
        return AccountConfig()
    }
}