package com.junjie.web.service

import com.junjie.core.model.Result
import com.junjie.share.service.UserInfoService
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class WebSocketService(private val simpMessagingTemplate: SimpMessagingTemplate, private val userInfoService: UserInfoService,
                       private val commentMessageService: CommentMessageService,
                       private val followMessageService: FollowMessageService,
                       private val messageSettingsService: MessageSettingsService,
                       private val replyMessageService: ReplyMessageService) {
    @Async
    fun sendComment(criticId: Int, authorId: Int) {
        if (criticId == authorId) return
        val messageSettings = messageSettingsService.get(authorId)
        val countUnread = commentMessageService.countUnread(authorId)
        if (messageSettings.followStatus) {
            simpMessagingTemplate.convertAndSendToUser(authorId.toString(), "/comment/send", Result<Any>(200, userInfoService.get(criticId).nickname + "评论了你的作品", countUnread))
        } else {
            simpMessagingTemplate.convertAndSendToUser(authorId.toString(), "/comment/send", Result<Any>(200, null, countUnread))
        }
    }

    @Async
    fun sendReply(answererId: Int, criticId: Int) {
        if (answererId == criticId) return
        val messageSettings = messageSettingsService.get(criticId)
        val countUnread = replyMessageService.countUnread(criticId)
        if (messageSettings.followStatus) {
            simpMessagingTemplate.convertAndSendToUser(criticId.toString(), "/reply/send", Result<Any>(200, userInfoService.get(answererId).nickname + "回复了你的评论", countUnread))
        } else {
            simpMessagingTemplate.convertAndSendToUser(criticId.toString(), "/reply/send", Result<Any>(200, null, countUnread))
        }
    }

    @Async
    fun sendFollowingFocus(followerId: Int, followingId: Int) {
        val messageSettings = messageSettingsService.get(followingId)
        val countUnread = followMessageService.countUnread(followingId)
        if (messageSettings.followStatus) {
            simpMessagingTemplate.convertAndSendToUser(followingId.toString(), "/following/focus", Result<Any>(200, userInfoService.get(followerId).nickname + "关注了你", countUnread))
        } else {
            simpMessagingTemplate.convertAndSendToUser(followingId.toString(), "/following/focus", Result<Any>(200, null, countUnread))
        }
    }
}