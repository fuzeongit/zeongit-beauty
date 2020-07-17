package com.zeongit.account.controller


import com.zeongit.account.dto.UserInfoDto
import com.zeongit.account.service.UserService
import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserId
import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.database.account.entity.UserInfo
import com.zeongit.share.service.UserInfoService
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("userInfo")
class UserInfoController(
        private val userService: UserService,
        private val userInfoService: UserInfoService) {

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

    /**
     * 获取用户信息
     */
    @Auth
    @GetMapping("getModifiedPasswordDate")
    @RestfulPack
    fun getModifiedPasswordDate(@CurrentUserInfoId id: Int): Date {
        val info = userInfoService.get(id)
        return userService.get(info.userId).lastModifiedDate!!
    }
}