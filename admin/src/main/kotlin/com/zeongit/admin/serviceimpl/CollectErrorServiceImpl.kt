package com.zeongit.admin.serviceimpl

import com.zeongit.admin.service.CollectErrorService
import com.zeongit.data.database.admin.dao.CollectErrorDao
import com.zeongit.data.database.admin.entity.CollectError
import org.springframework.stereotype.Service

@Service
class CollectErrorServiceImpl(private val collectErrorDao: CollectErrorDao) : CollectErrorService {
    override fun save(collectError: CollectError): CollectError {
        return collectErrorDao.save(collectError)
    }
}