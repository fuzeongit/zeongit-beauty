package com.zeongit.web.core.communal

import com.zeongit.share.feign.account.vo.UserInfo
import com.zeongit.web.service.FollowService
import com.zeongit.web.service.UserInfoService
import com.zeongit.web.vo.UserInfoVo

abstract class UserInfoVoAbstract {
    abstract val userInfoService: UserInfoService
    abstract val followService: FollowService

    fun getUserVo(targetId: Int, userId: Int? = null): UserInfoVo {
        return getUserVo(userInfoService.get(targetId), userId)
    }

    fun getUserVo(user: UserInfo, userId: Int? = null): UserInfoVo {
        val infoVo = UserInfoVo(user)
        infoVo.focus = followService.exists(userId, user.id!!)
        return infoVo
    }
}