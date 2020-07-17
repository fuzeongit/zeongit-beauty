package com.zeongit.web.core.configurer

import com.zeongit.web.core.component.SchedulingComponent
import com.zeongit.web.service.CommentMessageService
import com.zeongit.web.service.FollowMessageService
import com.zeongit.web.service.ReplyMessageService
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