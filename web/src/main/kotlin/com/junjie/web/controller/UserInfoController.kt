package com.junjie.web.controller


import com.junjie.core.annotations.Auth
import com.junjie.core.annotations.CurrentUserInfoId
import com.junjie.core.annotations.RestfulPack
import com.junjie.share.database.account.entity.UserInfo
import com.junjie.share.service.UserInfoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("userInfo")
class UserInfoController(private val userInfoService: UserInfoService) {
    /**
     * 获取用户信息
     */
    @Auth
    @GetMapping("get")
    @RestfulPack
    fun get(@CurrentUserInfoId id: Int): UserInfo {
        return userInfoService.get(id)
    }
}