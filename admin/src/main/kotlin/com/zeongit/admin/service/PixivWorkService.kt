package com.zeongit.admin.service

import com.zeongit.data.database.admin.entity.PixivWork


interface PixivWorkService {
    fun save(pixivWork: PixivWork): PixivWork

    fun existsByPixivId(pixivId: String): Boolean

    fun getByPixivId(pixivId: String): PixivWork
}