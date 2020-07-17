package com.zeongit.account.controller



import com.zeongit.account.component.AccountConfig
import com.zeongit.account.dto.SendCodeDto
import com.zeongit.account.dto.UpdateDto
import com.zeongit.account.dto.UserDto
import com.zeongit.account.service.UserService
import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.constant.BaseConstant
import com.zeongit.share.enum.VerificationCodeOperation
import com.zeongit.share.exception.PermissionException
import com.zeongit.share.exception.ProgramException
import com.zeongit.share.model.Result
import com.zeongit.share.util.JwtUtil
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
                     private val userService: UserService) {

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
                userService.existsByPhone(phone) && throw ProgramException("手机号码已存在")
//                smsComponent.sendVerificationCode(phone, verificationCode)
                redisTemplate.opsForValue().set(String.format(accountConfig.registerVerificationCodePrefix, phone), verificationCode, accountConfig.verificationCodeTimeout, TimeUnit.MILLISECONDS)
                Result(200, null, verificationCode)
            }
            VerificationCodeOperation.FORGET -> {
                !userService.existsByPhone(phone) && throw ProgramException("手机号码不存在")
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
        val user = userService.signUp(phone, dto.password!!)
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
        val user = userService.signIn(dto.phone!!, dto.password!!)
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
        userService.forgot(phone, dto.password!!)
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
        return JwtUtil.createJWT(claimMap, nowMillis, accountConfig.jwtExpiresSecond, BaseConstant.JWT_SECRET)
    }
}