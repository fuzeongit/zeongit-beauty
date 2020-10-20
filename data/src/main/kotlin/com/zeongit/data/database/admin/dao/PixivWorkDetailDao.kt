package com.zeongit.data.database.admin.dao

import com.zeongit.data.database.admin.entity.PixivWorkDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PixivWorkDetailDao : JpaRepository<PixivWorkDetail, Int> {
    fun getByName(name: String): Optional<PixivWorkDetail>

    fun findAllByDownload(download: Boolean): List<PixivWorkDetail>

    fun findAllByPixivId(pixivId: String): List<PixivWorkDetail>
}