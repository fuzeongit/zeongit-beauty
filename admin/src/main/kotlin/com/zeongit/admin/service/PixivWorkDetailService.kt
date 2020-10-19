package com.zeongit.admin.service

import com.zeongit.data.database.admin.entity.PixivWorkDetail


interface PixivWorkDetailService {
    fun save(pixivWorkDetail: PixivWorkDetail): PixivWorkDetail

    fun getByName(name: String): PixivWorkDetail
}