package com.zeongit.web.service

import com.zeongit.data.database.primary.entity.Footprint
import com.zeongit.data.database.primary.entity.UserBlackHole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface UserBlackHoleService {
    fun paging(pageable: Pageable, userId: Int, startDate: Date? = null, endDate: Date? = null): Page<UserBlackHole>

    fun list(userId: Int, startDate: Date? = null, endDate: Date? = null): List<UserBlackHole>

    fun save(targetId: Int): UserBlackHole

    fun remove(userId: Int, targetId: Int): Boolean

    fun get(userId: Int, targetId: Int): UserBlackHole
}