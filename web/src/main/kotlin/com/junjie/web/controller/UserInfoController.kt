package com.junjie.web.controller


import com.junjie.core.annotations.CurrentUserInfoId
import com.junjie.core.annotations.RestfulPack
import com.junjie.core.exception.SignInException
import com.junjie.share.service.UserInfoService
import com.junjie.web.core.communal.UserInfoVoAbstract
import com.junjie.web.service.FollowService
import com.junjie.web.vo.UserInfoVo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("userInfo")
class UserInfoController(override val userInfoService: UserInfoService,
                         override val followService: FollowService) : UserInfoVoAbstract() {
    /**
     * 获取用户信息
     */
    @GetMapping("get")
    @RestfulPack
    fun get(@CurrentUserInfoId id: Int?, targetId: Int?): UserInfoVo {
        (id == null && targetId == null) && throw SignInException("请重新登录")
        return getUserVo(targetId ?: id!!, id)
    }
}