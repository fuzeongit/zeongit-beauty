package com.zeongit.admin.serviceimpl

import com.zeongit.admin.service.PixivPictureService
import com.zeongit.share.exception.NotFoundException
import com.zeongit.data.constant.TransferState
import com.zeongit.data.database.admin.dao.PixivPictureDao
import com.zeongit.data.database.admin.dao.PixivUserDao
import com.zeongit.data.database.admin.entity.PixivPicture
import com.zeongit.data.database.admin.entity.PixivUser
import org.springframework.stereotype.Service

@Service
class PixivPictureServiceImpl(
        private val pixivPictureDao: PixivPictureDao,
        private val pixivUserDao: PixivUserDao
) : PixivPictureService {
    override fun save(pixivPicture: PixivPicture): PixivPicture {
        return pixivPictureDao.save(pixivPicture)
    }

    override fun saveAll(pixivPictureList: List<PixivPicture>): List<PixivPicture> {
        return pixivPictureDao.saveAll(pixivPictureList)
    }

    override fun listByState(state: TransferState): List<PixivPicture> {
        return pixivPictureDao.findAllByState(state)
    }

    override fun listByPixivId(pixivId: String): List<PixivPicture> {
        return pixivPictureDao.findAllByPixivId(pixivId)
    }

    override fun existsAccountByPixivUserId(pixivUserId: String): Boolean {
        return pixivUserDao.existsByPixivUserId(pixivUserId)
    }

    override fun getAccountByPixivUserId(pixivUserId: String): PixivUser {
        return pixivUserDao.findOneByPixivUserId(pixivUserId).orElseThrow { NotFoundException("用户不存在") }
    }

    override fun saveAccount(accountId: Int, pixivUserId: String): PixivUser {
        return pixivUserDao.save(PixivUser(accountId, pixivUserId))
    }

    override fun getByPictureId(PictureId: Int): PixivPicture {
        return pixivPictureDao.findOneByPictureId(PictureId).orElseThrow { NotFoundException("采集图片不存在") }
    }
}