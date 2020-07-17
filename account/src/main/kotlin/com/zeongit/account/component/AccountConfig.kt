package com.zeongit.account.component

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*

/**
 * @author fjj
 * 配置
 */
@Component
@ConfigurationProperties("account")
class AccountConfig {
    //注册验证码存redis的key前缀
    lateinit var registerVerificationCodePrefix: String
    //忘记密码验证码存redis的key前缀
    lateinit var forgetVerificationCodePrefix: String
    //验证码过期毫秒数
    var verificationCodeTimeout: Long = 0

    var jwtExpiresSecond: Long = 0

    lateinit var updatePasswordTimePrefix: String
}
