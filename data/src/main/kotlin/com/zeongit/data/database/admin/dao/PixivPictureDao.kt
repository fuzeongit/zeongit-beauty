package com.zeongit.data.database.admin.dao

import com.zeongit.data.constant.TransferState
import com.zeongit.data.database.admin.entity.PixivPicture
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PixivPictureDao : JpaRepository<PixivPicture, Int> {
    fun findAllByPixivId(pixivId: String): List<PixivPicture>

    fun findAllByState(state: TransferState): List<PixivPicture>

    fun findOneByPictureId(pictureId: Int): Optional<PixivPicture>
}