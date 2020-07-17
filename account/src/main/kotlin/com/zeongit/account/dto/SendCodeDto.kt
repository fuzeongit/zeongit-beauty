package com.zeongit.account.dto

import com.zeongit.share.exception.ProgramException
import com.zeongit.share.util.RegexUtil
import com.zeongit.share.enum.VerificationCodeOperation

class SendCodeDto {
    var phone: String = ""
//        get() {
//            if (!RegexUtil.checkMobile(field)) {
//                throw ProgramException("请输入正确的手机号码")
//            }
//            return field
//        }

    lateinit var verificationCodeOperation: VerificationCodeOperation
}