package com.junjie.account.controller

import com.junjie.account.dto.UserInfoDto
import com.junjie.account.service.UserInfoService
import com.junjie.core.annotations.Auth
import com.junjie.core.annotations.CurrentUserId
import com.junjie.core.annotations.CurrentUserInfoId
import com.junjie.core.annotations.RestfulPack
import com.junjie.core.exception.NotFoundException
import com.junjie.data.constant.Gender
import com.junjie.data.database.account.entity.User
import com.junjie.data.database.account.entity.UserInfo
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("userInfo")
class UserInfoController(private val userInfoService: UserInfoService) {

    /**
     * 创建及修改
     */
    @Auth(true)
    @PostMapping("save")
    @RestfulPack
    fun save(@CurrentUserId userId: Int, @RequestBody userInfoDto: UserInfoDto): UserInfo {
        val info = try {
            userInfoService.getByUserId(userId)
        } catch (e: NotFoundException) {
            val info = UserInfo()
            info.userId = userId
            info
        }
        info.gender = userInfoDto.gender ?: info.gender
        info.birthday = userInfoDto.birthday ?: info.birthday
        info.nickname = userInfoDto.nickname ?: info.nickname
        info.introduction = userInfoDto.introduction ?: info.introduction
        info.country = userInfoDto.country ?: info.country
        info.province = userInfoDto.province ?: info.province
        info.city = userInfoDto.city ?: info.city
        info.avatarUrl = userInfoDto.avatarUrl ?: info.avatarUrl
        info.background = userInfoDto.background ?: info.background
        return userInfoService.save(info)
    }

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