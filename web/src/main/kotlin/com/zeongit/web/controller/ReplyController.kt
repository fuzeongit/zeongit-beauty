package com.zeongit.web.controller

import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.data.database.primary.entity.Reply
import com.zeongit.data.database.primary.entity.ReplyMessage
import com.zeongit.web.core.communal.UserInfoVoAbstract
import com.zeongit.web.service.*
import com.zeongit.web.vo.ReplyVo
import org.springframework.web.bind.annotation.*

/**
 * @author fjj
 * 回复的控制器
 */
@RestController
@RequestMapping("reply")
class ReplyController(private val replyService: ReplyService,
                      private val replyMessageService: ReplyMessageService,
                      private val webSocketService: WebSocketService,
                      override val userInfoService: UserInfoService,
                      override val followService: FollowService) : UserInfoVoAbstract() {

    class SaveDto {
        var commentId: Int = 0
        var authorId: Int = 0
        var criticId: Int = 0
        var pictureId: Int = 0
        lateinit var content: String
    }

    /**
     * 发表评论
     */
    @Auth
    @PostMapping("save")
    @RestfulPack
    fun save(@CurrentUserId answererId: Int, @RequestBody dto: SaveDto): ReplyVo {
        dto.content.isEmpty() && throw Exception("回复不能为空")
        val reply = Reply(dto.commentId, dto.authorId, dto.criticId, dto.pictureId, dto.content)
        val vo = ReplyVo(replyService.save(reply), getUserVo(dto.criticId, answererId), getUserVo(answererId, answererId))
        val replyMessage = ReplyMessage(vo.commentId, vo.id, vo.authorId, vo.pictureId, vo.criticId, vo.content)
        replyMessageService.save(replyMessage)
        webSocketService.sendReply(answererId, dto.criticId)
        return vo
    }

    /**
     * 获取列表
     */
    @GetMapping("list")
    @RestfulPack
    fun list(@CurrentUserId userId: Int, commentId: Int): List<ReplyVo> {
        return replyService.list(commentId).map {
            ReplyVo(it, getUserVo(it.criticId, userId), getUserVo(it.createdBy!!, userId))
        }
    }
}