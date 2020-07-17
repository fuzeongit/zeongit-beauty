package com.zeongit.web.service

import com.zeongit.share.database.account.entity.UserInfo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserInfoService {
    //保存
    fun save(info: UserInfo): UserInfo

    //获取用户信息
    fun get(id: Int): UserInfo

    //根据账户id获取用户信息
    fun getByUserId(userId: Int): UserInfo

    //获取全部用户
    fun list(name: String? = null): List<UserInfo>

    //获取分页
    fun paging(pageable: Pageable, name: String?, accountIdList: List<String>): Page<UserInfo>
}