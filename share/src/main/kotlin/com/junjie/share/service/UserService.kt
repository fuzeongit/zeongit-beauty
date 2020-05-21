package com.junjie.share.service

import com.junjie.share.database.account.entity.User


interface UserService {
    //保存
    fun save(user: User): User

    //根据id获取
    fun get(id: Int): User

    //根据手机获取
    fun getByPhone(phone: String): User

    //是否存在手机
    fun existsByPhone(phone: String): Boolean

    //注册
    fun signUp(phone: String, password: String): User

    //登录
    fun signIn(phone: String, password: String): User

    //修改密码
    fun forgot(phone: String, password: String): User

    //获取列表
    fun listByPhoneLike(phone: String): List<User>
}