package com.junjie.share.dto

import com.junjie.core.exception.ProgramException
import com.junjie.core.util.RegexUtil
import com.junjie.share.constant.VerificationCodeOperation

class SendCodeDto {
    var phone: String = ""
        get() {
            if (!RegexUtil.checkMobile(field)) {
                throw ProgramException("请输入正确的手机号码")
            }
            return field
        }

    lateinit var verificationCodeOperation: VerificationCodeOperation
}