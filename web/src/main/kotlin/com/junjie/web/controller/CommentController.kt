package com.junjie.web.controller

import com.junjie.core.annotations.Auth
import com.junjie.core.annotations.CurrentUserInfoId
import com.junjie.core.annotations.RestfulPack
import com.junjie.data.database.primary.entity.Comment
import com.junjie.data.database.primary.entity.CommentMessage
import com.junjie.share.service.UserInfoService
import com.junjie.web.service.CommentMessageService
import com.junjie.web.service.CommentService
import com.junjie.web.service.FollowService
import com.junjie.web.core.communal.UserVoAbstract
import com.junjie.web.service.WebSocketService
import com.junjie.web.vo.CommentVo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

/**
 * @author fjj
 * 评论的控制器
 */
@RestController
@RequestMapping("comment")
class CommentController(private val commentService: CommentService,
                        private val commentMessageService: CommentMessageService,
                        private val webSocketService: WebSocketService,
                        override val userInfoService: UserInfoService,
                        override val followService: FollowService) : UserVoAbstract() {

    class SaveDto {
        var authorId: Int = 0

        var pictureId: Int = 0

        lateinit var content: String
    }

    /**
     * 发表评论
     */
    @Auth
    @PostMapping("save")
    @RestfulPack
    fun save(@CurrentUserInfoId criticId: Int, @RequestBody dto: SaveDto): CommentVo {
        val comment = Comment(dto.authorId, dto.pictureId, dto.content)
        val vo = CommentVo(
                commentService.save(comment),
                getUserVo(dto.authorId, criticId),
                getUserVo(criticId, criticId)
        )
        val commentMessage = CommentMessage(vo.id, vo.authorId, vo.pictureId, vo.content)
        commentMessageService.save(commentMessage)
        webSocketService.sendComment(criticId, dto.authorId)
        return vo
    }

    /**
     * 获取4条
     */
    @GetMapping("listTop4")
    @RestfulPack
    fun listTop4(@CurrentUserInfoId userId: Int?, pictureId: Int): List<CommentVo> {
        return getListVo(commentService.listTop4(pictureId), userId)
    }

    /**
     * 获取列表
     */
    @GetMapping("list")
    @RestfulPack
    fun list(@CurrentUserInfoId userId: Int?, pictureId: Int): List<CommentVo> {
        return getListVo(commentService.list(pictureId), userId)
    }

    /**
     * 分页获取
     */
    @GetMapping("pagingByPictureId")
    @RestfulPack
    fun pagingByPictureId(@CurrentUserInfoId userId: Int?, pictureId: Int, @PageableDefault(value = 20) pageable: Pageable): Page<CommentVo> {
        return getPageVo(commentService.paging(pictureId, pageable), userId)
    }

    private fun getPageVo(page: Page<Comment>, userId: Int? = null): Page<CommentVo> {
        val commentVoList = getListVo(page.content, userId)
        return PageImpl(commentVoList, page.pageable, page.totalElements)
    }

    private fun getListVo(list: List<Comment>, userId: Int? = null): List<CommentVo> {
        return list.map {
            CommentVo(
                    it,
                    getUserVo(it.authorId, userId),
                    getUserVo(it.createdBy!!, userId)
            )
        }
    }
}