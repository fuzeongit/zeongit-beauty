package com.junjie.web.service

import com.junjie.data.database.primary.entity.Footprint
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FootprintService {
    fun get(userId: Int, pictureId: Int): Footprint

    fun exists(userId: Int, pictureId: Int): Boolean

    fun save(pictureId: Int): Footprint

    fun update(userId: Int, pictureId: Int): Footprint

    fun remove(userId: Int, pictureId: Int): Boolean

    fun countByPictureId(pictureId: Int): Long

    fun pagingByUserId(userId: Int, pageable: Pageable): Page<Footprint>

    fun pagingByPictureId(pictureId: Int, pageable: Pageable): Page<Footprint>
}