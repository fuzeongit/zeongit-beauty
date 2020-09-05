package com.zeongit.web.controller


import com.zeongit.share.annotations.CurrentUserId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.SignInException
import com.zeongit.web.core.communal.UserInfoVoAbstract
import com.zeongit.web.service.*
import com.zeongit.web.vo.UserInfoVo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("userInfo")
class UserInfoController(override val userInfoService: UserInfoService,
                         override val followService: FollowService
) : UserInfoVoAbstract() {
    /**
     * 获取用户信息
     */
    @GetMapping("get")
    @RestfulPack
    fun get(@CurrentUserId id: Int?, targetId: Int?): UserInfoVo {
        (id == null && targetId == null) && throw SignInException("请重新登录")
        return getUserVo(targetId ?: id!!, id)
    }
}