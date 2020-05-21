package com.junjie.web.core.configurer

import com.junjie.web.core.component.SchedulingComponent
import com.junjie.web.service.CommentMessageService
import com.junjie.web.service.FollowMessageService
import com.junjie.web.service.ReplyMessageService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 定时器的配置
 * @author fjj
 */
@Configuration
class SchedulingConfigurer(
        private val commentMessageService: CommentMessageService,
        private val replyMessageService: ReplyMessageService,
        private val followMessageService: FollowMessageService) {

    @Bean
    internal fun schedulingComponent(): SchedulingComponent {
        return SchedulingComponent(
                commentMessageService,
                replyMessageService,
                followMessageService)
    }
}