package com.junjie.user.serviceimpl

import com.junjie.account.service.UserService
import com.junjie.core.exception.NotFoundException
import com.junjie.core.exception.PermissionException
import com.junjie.core.exception.SignInException
import com.junjie.data.database.account.dao.UserDao
import com.junjie.data.database.account.entity.User
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(private val userDao: UserDao) : UserService {

    //保存
    override fun save(user: User): User {
        return userDao.save(user)
    }

    //根据id获取
    override fun get(id: Int): User {
        return userDao.findById(id).orElseThrow { NotFoundException("账号不存在") }
    }

    //根据手机获取
    override fun getByPhone(phone: String): User {
        return userDao.findOneByPhone(phone).orElseThrow { NotFoundException("账号不存在") }
    }

    //是否存在手机
    override fun existsByPhone(phone: String): Boolean {
        return userDao.existsByPhone(phone)
    }

    //注册
    override fun signUp(phone: String, password: String): User {
        existsByPhone(phone) && throw PermissionException("手机号已存在")
        val user = User(phone, password)
        return userDao.save(user)
    }

    //登录
    override fun signIn(phone: String, password: String): User {
        !existsByPhone(phone) && throw PermissionException("手机号不存在")
        return userDao.findOneByPhoneAndPassword(phone, password).orElseThrow { SignInException("账号密码不正确") }
    }

    //修改密码
    override fun forgot(phone: String, password: String): User {
        val user = getByPhone(phone)
        user.password = password
        return save(user)
    }

    //获取列表
    override fun listByPhoneLike(phone: String): List<User> {
        return userDao.findAllByPhoneLike("%$phone%")
    }
}