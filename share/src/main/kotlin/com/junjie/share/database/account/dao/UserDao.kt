package com.junjie.share.database.account.dao

import com.junjie.share.database.account.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository

interface UserDao : JpaRepository<User, Int> {
    fun existsByPhone(phone: String): Boolean

    fun findOneByPhone(phone: String): Optional<User>

    fun findOneByPhoneAndPassword(phone: String, password: String): Optional<User>

    fun findAllByPhoneLike(phone: String): List<User>
}