package com.zeongit.admin.service

import com.zeongit.data.database.admin.entity.PixivWorkDetail


interface PixivWorkDetailService {
    fun save(pixivWorkDetail: PixivWorkDetail): PixivWorkDetail

    fun getByName(name: String): PixivWorkDetail

    fun listByPixivId(pixivId: String): List<PixivWorkDetail>

    fun listByDownload(download: Boolean): List<PixivWorkDetail>

    fun saveAll(pixivWorkDetailList: List<PixivWorkDetail>): List<PixivWorkDetail>

}