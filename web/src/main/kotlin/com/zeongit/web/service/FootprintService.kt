package com.zeongit.web.service

import com.zeongit.data.database.primary.entity.Collection
import com.zeongit.data.database.primary.entity.Footprint
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface FootprintService {
    fun get(userId: Int, pictureId: Int): Footprint

    fun exists(userId: Int, pictureId: Int): Boolean

    fun save(pictureId: Int): Footprint

    fun update(userId: Int, pictureId: Int): Footprint

    fun remove(userId: Int, pictureId: Int): Boolean

    fun countByPictureId(pictureId: Int): Long

    fun paging(pageable: Pageable, userId: Int, startDate: Date? = null, endDate: Date? = null): Page<Footprint>

    fun pagingByPictureId(pageable: Pageable, pictureId: Int, startDate: Date? = null, endDate: Date? = null): Page<Footprint>
}