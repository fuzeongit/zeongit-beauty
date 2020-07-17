package com.zeongit.admin.service

import com.zeongit.data.constant.TransferState
import com.zeongit.data.database.admin.entity.PixivPicture
import com.zeongit.data.database.admin.entity.PixivUser

interface PixivPictureService {
    fun save(pixivPicture: PixivPicture): PixivPicture

    fun saveAll(pixivPictureList: List<PixivPicture>): List<PixivPicture>

    fun listByState(state: TransferState): List<PixivPicture>

    fun listByPixivId(pixivId: String): List<PixivPicture>

    fun existsAccountByPixivUserId(pixivUserId: String): Boolean

    fun getAccountByPixivUserId(pixivUserId: String): PixivUser

    fun saveAccount(accountId: Int, pixivUserId: String): PixivUser

    fun getByPictureId(PictureId: Int): PixivPicture
}