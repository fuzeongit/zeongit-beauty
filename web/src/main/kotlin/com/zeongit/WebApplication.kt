package com.zeongit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import springfox.documentation.swagger2.annotations.EnableSwagger2


@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableWebSocketMessageBroker
@EnableSwagger2
@EnableScheduling
@SpringBootApplication
@EnableEurekaClient
class WebApplication : SpringBootServletInitializer() {
    override fun configure(builder: SpringApplicationBuilder): SpringApplicationBuilder {
        // 设置启动类，用于独立tomcat运行的入口
        System.setProperty("es.set.netty.runtime.available.processors", "false")
        return builder.sources(WebApplication::class.java)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty("es.set.netty.runtime.available.processors", "false")
            runApplication<WebApplication>(*args)
        }
    }
}
