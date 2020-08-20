package com.zeongit.web.service

import com.zeongit.data.database.primary.entity.PictureBlackHole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface PictureBlackHoleService {
    fun paging(pageable: Pageable, userId: Int, startDate: Date? = null, endDate: Date? = null): Page<PictureBlackHole>

    fun list(userId: Int, startDate: Date? = null, endDate: Date? = null): List<PictureBlackHole>

    fun save(targetId: Int): PictureBlackHole

    fun remove(userId: Int, targetId: Int): Boolean

    fun get(userId: Int, targetId: Int): PictureBlackHole
}