package com.zeongit.web.controller

import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.ProgramException
import com.zeongit.share.exception.SignInException
import com.zeongit.data.constant.FollowState
import com.zeongit.data.database.primary.entity.FollowMessage
import com.zeongit.web.service.FollowMessageService
import com.zeongit.web.service.FollowService
import com.zeongit.web.core.communal.UserInfoVoAbstract
import com.zeongit.web.service.UserInfoService
import com.zeongit.web.service.WebSocketService
import com.zeongit.web.vo.UserInfoVo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

/**
 * @author fjj
 * 关注人的控制器
 */
@RestController
@RequestMapping("following")
class FollowingController(private val followMessageService: FollowMessageService,
                          private val webSocketService: WebSocketService,
                          override val userInfoService: UserInfoService,
                          override val followService: FollowService) : UserInfoVoAbstract() {

    class FocusDto {
        var followingId: Int = 0
    }

    class UnFocusDto {
        var followingIdList: List<Int>? = null
            get() {
                if (field == null || field!!.isEmpty()) {
                    throw ProgramException("请选择一个关注")
                }
                return field
            }
    }

    @Auth
    @PostMapping("focus")
    @RestfulPack
    fun focus(@CurrentUserInfoId followerId: Int, @RequestBody dto: FocusDto): FollowState {
        val followingId = dto.followingId
        if (followerId == followingId) {
            throw ProgramException("不能关注自己")
        }
        val focus = followService.exists(followerId, followingId)
        return if (focus == FollowState.STRANGE) {
            val follow = followService.save(followingId)
            val followMessage = FollowMessage(follow.followingId)
            followMessageService.save(followMessage)
            webSocketService.sendFollowingFocus(followerId, followingId)
            FollowState.CONCERNED
        } else {
            followService.remove(followerId, followingId)
            FollowState.STRANGE
        }
    }

    /**
     * 取消关注一组
     */
    @Auth
    @PostMapping("unFocus")
    @RestfulPack
    fun unFocus(@CurrentUserInfoId followerId: Int, @RequestBody() dto: UnFocusDto): Boolean {
        for (followingId in dto.followingIdList!!) {
            try {
                followService.remove(followerId, followingId)
            } catch (e: Exception) {
            }
        }
        return false
    }

    /**
     * 获取关注列表
     */
    @GetMapping("paging")
    @RestfulPack
    fun paging(@CurrentUserInfoId followerId: Int?, targetId: Int?, @PageableDefault(value = 20) pageable: Pageable): Page<UserInfoVo> {
        if (followerId == null && targetId == null) throw SignInException("请重新登录")
        val page = followService.pagingByFollowerId(pageable, targetId ?: followerId!!)
        val userVoList = page.content.map {
            val userVo = UserInfoVo(userInfoService.get(it.followingId))
            userVo.focus = followService.exists(followerId, userVo.id)
            userVo
        }
        return PageImpl(userVoList, page.pageable, page.totalElements)
    }
}