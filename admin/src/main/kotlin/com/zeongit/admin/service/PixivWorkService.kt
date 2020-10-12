package com.zeongit.admin.service

import com.zeongit.data.database.admin.entity.PixivWork
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface PixivWorkService {
    fun save(pixivWork: PixivWork): PixivWork

    fun existsByPixivId(pixivId: String): Boolean

    fun getByPixivId(pixivId: String): PixivWork

    fun paging(pageable: Pageable): Page<PixivWork>
}