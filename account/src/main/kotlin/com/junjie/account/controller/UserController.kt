package com.junjie.account.controller

import com.junjie.account.component.AccountConfig
import com.junjie.account.dto.SendCodeDto
import com.junjie.account.dto.UserDto
import com.junjie.account.dto.UpdateDto
import com.junjie.account.service.UserService
import com.junjie.core.annotations.Auth
import com.junjie.core.annotations.RestfulPack
import com.junjie.core.exception.PermissionException
import com.junjie.core.exception.ProgramException
import com.junjie.core.model.Result
import com.junjie.core.util.JwtUtil
import com.junjie.data.constant.VerificationCodeOperation
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("user")
class UserController(private val accountConfig: AccountConfig,
                     private val redisTemplate: StringRedisTemplate,
                     private val accountService: UserService) {

    /**
     * 发送验证码
     */
    @PostMapping("sendCode")
    @RestfulPack
    fun sendCode(@RequestBody dto: SendCodeDto): Result<String> {
        var verificationCode = ""
        while (verificationCode.length < 6) {
            verificationCode += Random().nextInt(10).toString()
        }
        val phone = dto.phone
        return when (dto.verificationCodeOperation) {
            VerificationCodeOperation.REGISTER -> {
                accountService.existsByPhone(phone) && throw ProgramException("手机号码已存在")
//                smsComponent.sendVerificationCode(phone, verificationCode)
                redisTemplate.opsForValue().set(String.format(accountConfig.registerVerificationCodePrefix, phone), verificationCode, accountConfig.verificationCodeTimeout, TimeUnit.MILLISECONDS)
                Result(200, null, verificationCode)
            }
            VerificationCodeOperation.FORGET -> {
                !accountService.existsByPhone(phone) && throw ProgramException("手机号码不存在")
//                smsComponent.sendVerificationCode(phone, verificationCode)
                redisTemplate.opsForValue().set(String.format(accountConfig.forgetVerificationCodePrefix, phone), verificationCode, accountConfig.verificationCodeTimeout, TimeUnit.MILLISECONDS)
                Result(200, null, verificationCode)
            }
            else -> {
                Result(200, null, "888888")
            }
        }
    }

    /***
     * 注册
     */
    @PostMapping("/signUp")
    @RestfulPack
    fun signUp(@RequestBody dto: UpdateDto, response: HttpServletResponse): Boolean {
        val phone = dto.phone!!
        val redisCode = redisTemplate.opsForValue()[String.format(accountConfig.registerVerificationCodePrefix, phone)]
        dto.verificationCode != redisCode && throw ProgramException("验证码无效")
        //获取系统时间
        val nowMillis = System.currentTimeMillis()
        val user = accountService.signUp(phone, dto.password!!)
        //清理验证码
        redisTemplate.delete(String.format(accountConfig.registerVerificationCodePrefix, phone))
        //把修改密码时间放到redis
        redisTemplate.opsForValue().set(String.format(accountConfig.updatePasswordTimePrefix, user.id), nowMillis.toString())

        response.setHeader("token", generateToken(user.id?.toString()!!, nowMillis))
        return true
    }

    /**
     * 登录
     */
    @PostMapping("/signIn")
    @RestfulPack
    fun signIn(@RequestBody dto: UserDto, response: HttpServletResponse): Boolean {
        val user = accountService.signIn(dto.phone!!, dto.password!!)
        response.setHeader("token", generateToken(user.id?.toString()!!, System.currentTimeMillis()))
        return true
    }

    /**
     * 忘记密码
     */
    @PostMapping("/forgot")
    @RestfulPack
    fun forgot(@RequestBody dto: UpdateDto, response: HttpServletResponse): Boolean {
        val phone = dto.phone!!
        val redisCode = redisTemplate.opsForValue()[String.format(accountConfig.forgetVerificationCodePrefix, phone)]
        dto.verificationCode != redisCode && throw PermissionException("验证码无效")
        accountService.forgot(phone, dto.password!!)
        return true
    }

    /**
     * 检查登录
     */
    @Auth
    @PostMapping("/checkLogin")
    @RestfulPack
    fun checkLogin(): Boolean {
        return true
    }

    /**
     * 生成token
     */
    private fun generateToken(id: String, nowMillis: Long): String {
        val claimMap = hashMapOf<String, String>()
        claimMap["id"] = id
        return JwtUtil.createJWT(claimMap, nowMillis, accountConfig.jwtExpiresSecond, accountConfig.jwtSecretString)
    }
}