package com.zeongit.admin.service

import com.zeongit.data.database.admin.entity.NsfwLevel

interface NsfwLevelService {
    fun list(): List<NsfwLevel>
}