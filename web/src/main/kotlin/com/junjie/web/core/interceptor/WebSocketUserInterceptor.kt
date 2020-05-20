package com.junjie.web.core.interceptor

import com.junjie.account.component.AccountConfig
import com.junjie.account.service.UserInfoService
import com.junjie.account.service.UserService
import com.junjie.core.component.BaseConfig
import com.junjie.core.exception.SignInException
import com.junjie.core.util.DateUtil
import com.junjie.core.util.JwtUtil
import com.junjie.web.model.WebSocketUser
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import java.util.*


class WebSocketUserInterceptor(private val baseConfig: BaseConfig,
                               private val accountConfig: AccountConfig,
                               private val userService: UserService,
                               private val userInfoService: UserInfoService) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
        if (StompCommand.CONNECT == accessor!!.command) {
            try {
                //获取头信息
                val raw = message.headers[SimpMessageHeaderAccessor.NATIVE_HEADERS] as Map<*, *>
//                val token = raw["token"] as LinkedList<*>
                val token = (raw["token"] as LinkedList<*>)[0].toString()

                val claims = JwtUtil.parseJWT(token, accountConfig.jwtSecretString)
                val id = claims["id"] as Int
                //过期时间
                val exp = Date(claims["exp"]!!.toString().toLong() * 1000)
                //生成时间
                val nbf = Date(claims["nbf"]!!.toString().toLong() * 1000)

                val user = userService.get(id)

                if (DateUtil.getDistanceTimestamp(Date(), exp) < 0) {
                    throw SignInException("用户登录已过期")
                }
                if (DateUtil.getDistanceTimestamp(user.lastModifiedDate!!, nbf) < 0) {
                    throw SignInException("请重新登录")
                }

                accessor.user = WebSocketUser(userInfoService.getByUserId(id).id.toString())
            } catch (e: Exception) {
                accessor.user = WebSocketUser(baseConfig.notUUID)
            }
        }
//        else if (StompCommand.DISCONNECT == accessor.command) {
//            //点击断开连接，这里会执行两次，第二次执行的时候，message.getHeaders.size()=5,第一次是6。直接关闭浏览器，只会执行一次，size是5。
//            //println("断开连接 --- 拦截器")
//            //val vo = message.headers[SimpMessageHeaderAccessor.USER_HEADER] as WebSocketUser
//        }
        return message
    }
}