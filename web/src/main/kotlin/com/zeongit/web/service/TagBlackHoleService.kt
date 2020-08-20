package com.zeongit.web.service

import com.zeongit.data.database.primary.entity.TagBlackHole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface TagBlackHoleService {
    fun paging(pageable: Pageable, userId: Int, startDate: Date? = null, endDate: Date? = null): Page<TagBlackHole>

    fun list(userId: Int, startDate: Date? = null, endDate: Date? = null): List<TagBlackHole>

    fun save(tag: String): TagBlackHole

    fun remove(userId: Int, tag: String): Boolean

    fun get(userId: Int, tag: String): TagBlackHole

    fun exists(userId: Int, tag: String): Boolean

    fun listBlacklist(userId: Int? = null): MutableList<String>
}