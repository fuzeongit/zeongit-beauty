package com.zeongit.web.controller

import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.data.constant.MessageType
import com.zeongit.data.database.primary.entity.CommentMessage
import com.zeongit.data.database.primary.entity.FollowMessage
import com.zeongit.data.database.primary.entity.MessageSettings
import com.zeongit.data.database.primary.entity.ReplyMessage
import com.zeongit.web.service.*
import com.zeongit.web.core.communal.UserInfoVoAbstract
import com.zeongit.web.vo.CommentMessageVo
import com.zeongit.web.vo.FollowMessageVo
import com.zeongit.web.vo.ReplyMessageVo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("message")
class MessageController(private val commentMessageService: CommentMessageService,
                        private val replyMessageService: ReplyMessageService,
                        private val followMessageService: FollowMessageService,
                        private val messageSettingsService: MessageSettingsService,
                        override val userInfoService: UserInfoService,
                        override val followService: FollowService) : UserInfoVoAbstract() {

    class SaveDto {
        var commentStatus: Boolean = true
        var replyStatus: Boolean = true
        var followStatus: Boolean = true
    }

    @Auth
    @GetMapping("count")
    @RestfulPack
    fun count(@CurrentUserInfoId userId: Int, messageType: MessageType?): HashMap<MessageType, Long> {
        val vo = HashMap<MessageType, Long>()
        vo[MessageType.COMMENT] = commentMessageService.countUnread(userId)
        vo[MessageType.REPLY] = replyMessageService.countUnread(userId)
        vo[MessageType.FOLLOW] = followMessageService.countUnread(userId)
//        vo[MessageType.SYSTEM] = systemMessageService.countUnread(userId)
        return vo
    }

    @Auth
    @GetMapping("listUnread")
    @RestfulPack
    fun listUnread(@CurrentUserInfoId userId: Int, messageType: MessageType): List<Any> {
        if (messageType == MessageType.COMMENT) {
            return getCommentMessageListVo(commentMessageService.listUnread(userId))
        }
        if (messageType == MessageType.REPLY) {
            return getReplyMessageListVo(replyMessageService.listUnread(userId))
        }
        if (messageType == MessageType.FOLLOW) {
            return getFollowMessageListVo(followMessageService.listUnread(userId))
        }
//        if (messageType == MessageType.SYSTEM) {
//            return systemMessageService.listUnread(userId)
//        }
        return listOf()
    }

    @Auth
    @GetMapping("list")
    @RestfulPack
    fun list(@CurrentUserInfoId userId: Int, messageType: MessageType): List<Any> {
        if (messageType == MessageType.COMMENT) {
            return getCommentMessageListVo(commentMessageService.list(userId))
        }
        if (messageType == MessageType.REPLY) {
            return getReplyMessageListVo(replyMessageService.list(userId))
        }
        if (messageType == MessageType.FOLLOW) {
            return getFollowMessageListVo(followMessageService.list(userId))
        }
//        if (messageType == MessageType.SYSTEM) {
//            val list = systemMessageService.list(userId)
//            for (item in list) {
//                item.review && continue
//                item.review = true
//                systemMessageService.save(item)
//            }
//            return list
//        }
        return listOf()
    }

    @Auth
    @GetMapping("getSettings")
    @RestfulPack
    fun getSettings(@CurrentUserInfoId userId: Int): MessageSettings {
        return try {
            messageSettingsService.get(userId)
        } catch (e: Exception) {
            val newSettings = MessageSettings()
            messageSettingsService.save(newSettings)
        }
    }

    @Auth
    @PostMapping("saveSettings")
    @RestfulPack
    fun saveSettings(@CurrentUserInfoId userId: Int, @RequestBody dto: SaveDto): MessageSettings {
        val messageSettings = messageSettingsService.get(userId)
        messageSettings.commentStatus = dto.commentStatus
        messageSettings.replyStatus = dto.replyStatus
        messageSettings.followStatus = dto.followStatus
        return messageSettingsService.save(messageSettings)
    }

    private fun getCommentMessageListVo(list: List<CommentMessage>): List<CommentMessageVo> {
        val voList = mutableListOf<CommentMessageVo>()
        for (commentMessage in list) {
            voList.add(CommentMessageVo(commentMessage, getUserVo(commentMessage.createdBy!!, commentMessage.authorId)))
            commentMessage.review && continue
            commentMessage.review = true
            commentMessageService.save(commentMessage)
        }
        return voList
    }

    private fun getReplyMessageListVo(list: List<ReplyMessage>): List<ReplyMessageVo> {
        val voList = mutableListOf<ReplyMessageVo>()
        for (replyMessage in list) {
            voList.add(ReplyMessageVo(replyMessage, getUserVo(userInfoService.get(replyMessage.createdBy!!), replyMessage.criticId)))
            replyMessage.review && continue
            replyMessage.review = true
            replyMessageService.save(replyMessage)
        }
        return voList
    }

    private fun getFollowMessageListVo(list: List<FollowMessage>): List<FollowMessageVo> {
        val voList = mutableListOf<FollowMessageVo>()
        for (followMessage in list) {
            voList.add(FollowMessageVo(followMessage, getUserVo(userInfoService.get(followMessage.createdBy!!), followMessage.followingId)))
            followMessage.review && continue
            followMessage.review = true
            followMessageService.save(followMessage)
        }
        return voList
    }
}
