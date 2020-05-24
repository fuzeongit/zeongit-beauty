package com.junjie.admin.core.configurer

import com.junjie.qiniu.core.component.QiniuConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
}