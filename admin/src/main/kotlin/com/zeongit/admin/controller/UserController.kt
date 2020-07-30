package com.zeongit.admin.controller

import com.zeongit.admin.service.UserInfoService
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.database.account.entity.UserInfo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("user")
class UserController(
        private val userInfoService: UserInfoService) {

    @GetMapping("listInfo")
    @RestfulPack
    fun paging(name: String?): List<UserInfo> {
        return userInfoService.list(name)
    }
}
