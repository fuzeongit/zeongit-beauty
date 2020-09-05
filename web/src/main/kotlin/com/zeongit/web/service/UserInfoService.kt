package com.zeongit.web.service

import com.zeongit.share.feign.account.vo.UserInfo

interface UserInfoService {
    //获取用户信息
    fun get(id: Int): UserInfo
}