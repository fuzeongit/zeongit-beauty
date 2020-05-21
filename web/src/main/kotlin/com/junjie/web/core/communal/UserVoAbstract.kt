package com.junjie.web.core.communal



import com.junjie.share.database.account.entity.UserInfo
import com.junjie.share.service.UserInfoService
import com.junjie.web.service.FollowService
import com.junjie.web.vo.UserInfoVo

abstract class UserVoAbstract {
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