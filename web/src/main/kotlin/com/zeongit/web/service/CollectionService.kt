package com.zeongit.web.service

import com.zeongit.data.constant.CollectState
import com.zeongit.data.database.primary.entity.Collection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*


/**
 * 画的收藏服务
 *
 * @author fjj
 */

interface CollectionService {
    fun exists(userId: Int?, pictureId: Int): CollectState

    fun save(userId: Int, pictureId: Int): Collection

    fun remove(userId: Int, pictureId: Int): Boolean

    fun countByPictureId(pictureId: Int): Long

    fun paging(pageable: Pageable, userId: Int, startDate: Date? = null, endDate: Date? = null): Page<Collection>

    fun pagingByPictureId(pageable: Pageable, pictureId: Int, startDate: Date? = null, endDate: Date? = null): Page<Collection>
}