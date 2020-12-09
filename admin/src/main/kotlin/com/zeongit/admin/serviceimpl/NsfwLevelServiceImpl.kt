package com.zeongit.admin.serviceimpl

import com.zeongit.admin.service.NsfwLevelService
import com.zeongit.data.database.admin.dao.NsfwLevelDao
import com.zeongit.data.database.admin.entity.NsfwLevel
import org.springframework.stereotype.Service

@Service
class NsfwLevelServiceImpl(private val nsfwLevelDao: NsfwLevelDao) : NsfwLevelService {
    override fun list(): List<NsfwLevel> {
        return nsfwLevelDao.findAll()
    }
}