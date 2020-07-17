package com.zeongit.account.dto

import com.zeongit.share.exception.ProgramException
import com.zeongit.share.util.RegexUtil

open class UserDto {
    var phone: String? = null
//        get() {
//            if (field == null || !RegexUtil.checkMobile(field!!)) {
//                throw ProgramException("请输入正确的手机号码")
//            }
//            return field
//        }

    var password: String? = null
//        get() {
//            if (field == null || !RegexUtil.checkPassword(field!!)) {
//                throw ProgramException("请输入正确的密码（密码由数字，字母和下划线的6-16位字符组成）")
//            }
//            return field
//        }
}