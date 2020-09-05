package com.zeongit.web.serviceimpl

import com.zeongit.share.constant.ExceptionCodeConstant
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.exception.ProgramException
import com.zeongit.share.feign.account.service.UserInfoFeign
import com.zeongit.share.feign.account.vo.UserInfo
import com.zeongit.web.service.UserInfoService
import org.springframework.stereotype.Service

@Service
class UserInfoServiceImpl(private val userInfoFeign: UserInfoFeign) : UserInfoService {
    //获取用户信息
    override fun get(id: Int): UserInfo {
        val result = userInfoFeign.getByTargetId(id)
        return when (result.status) {
            ExceptionCodeConstant.NOT_FOUND -> throw  NotFoundException(result.message ?: "")
            ExceptionCodeConstant.SUCCESS -> result.data!!
            else -> throw  ProgramException(result.message ?: "")
        }
    }
}