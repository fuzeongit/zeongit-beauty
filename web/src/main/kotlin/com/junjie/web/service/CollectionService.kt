package com.junjie.web.service

import com.junjie.data.constant.CollectState
import com.junjie.data.database.primary.entity.Collection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


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

    fun pagingByUserId(userId: Int, pageable: Pageable): Page<Collection>

    fun pagingByPictureId(pictureId: Int, pageable: Pageable): Page<Collection>
}