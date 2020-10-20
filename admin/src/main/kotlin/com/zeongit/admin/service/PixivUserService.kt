package com.zeongit.admin.service

import com.zeongit.data.database.admin.entity.PixivUser

interface PixivUserService {
    fun existsByPixivUserId(pixivUserId: String): Boolean
    fun getByPixivUserId(pixivUserId: String): PixivUser
    fun save(accountId: Int, pixivUserId: String): PixivUser
}