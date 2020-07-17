package com.zeongit.web.controller

import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.SignInException
import com.zeongit.web.service.FollowService
import com.zeongit.web.core.communal.UserInfoVoAbstract
import com.zeongit.web.service.UserInfoService
import com.zeongit.web.vo.UserInfoVo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author fjj
 * 粉丝的控制器
 */
@RestController
@RequestMapping("follower")
class FollowerController(override val userInfoService: UserInfoService, override val followService: FollowService) : UserInfoVoAbstract() {
    /**
     * 获取粉丝列表
     */
    @GetMapping("paging")
    @RestfulPack
    fun paging(@CurrentUserInfoId followingId: Int?, targetId: Int?, @PageableDefault(value = 20) pageable: Pageable): Page<UserInfoVo> {
        if (followingId == null && targetId == null) throw SignInException("请重新登录")
        val page = followService.pagingByFollowingId(targetId ?: followingId!!, pageable)
        val userVoList = page.content.map {
            getUserVo(it.createdBy!!, followingId)
        }
        return PageImpl(userVoList, page.pageable, page.totalElements)
    }
}