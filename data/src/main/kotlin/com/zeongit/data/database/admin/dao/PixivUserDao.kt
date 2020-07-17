package com.zeongit.data.database.admin.dao

import com.zeongit.data.database.admin.entity.PixivUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PixivUserDao : JpaRepository<PixivUser, Int> {
    fun existsByUserId(userId: Int): Boolean

    fun existsByPixivUserId(pixivUserId: String): Boolean

    fun findOneByPixivUserId(pixivUserId: String): Optional<PixivUser>
}