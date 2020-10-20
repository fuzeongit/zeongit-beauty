package com.zeongit.admin.serviceimpl

import com.zeongit.admin.service.PixivUserService
import com.zeongit.share.exception.NotFoundException
import com.zeongit.data.database.admin.dao.PixivUserDao
import com.zeongit.data.database.admin.entity.PixivUser
import org.springframework.stereotype.Service

@Service
class PixivUserServiceImpl(
        private val pixivUserDao: PixivUserDao
) : PixivUserService {
    override fun existsByPixivUserId(pixivUserId: String): Boolean {
        return pixivUserDao.existsByPixivUserId(pixivUserId)
    }

    override fun getByPixivUserId(pixivUserId: String): PixivUser {
        return pixivUserDao.findOneByPixivUserId(pixivUserId).orElseThrow { NotFoundException("用户不存在") }
    }

    override fun save(accountId: Int, pixivUserId: String): PixivUser {
        return pixivUserDao.save(PixivUser(accountId, pixivUserId))
    }
}