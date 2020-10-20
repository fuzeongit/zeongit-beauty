package com.zeongit.admin.serviceimpl

import com.zeongit.admin.service.PixivWorkDetailService
import com.zeongit.data.database.admin.dao.PixivWorkDao
import com.zeongit.data.database.admin.dao.PixivWorkDetailDao
import com.zeongit.data.database.admin.entity.PixivWork
import com.zeongit.data.database.admin.entity.PixivWorkDetail
import com.zeongit.share.exception.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PixivWorkDetailServiceImpl(private val pixivWorkDetailDao: PixivWorkDetailDao) : PixivWorkDetailService {
    override fun save(pixivWorkDetail: PixivWorkDetail): PixivWorkDetail {
        return pixivWorkDetailDao.save(pixivWorkDetail)
    }

    override fun getByName(name: String): PixivWorkDetail {
        return pixivWorkDetailDao.getByName(name).orElseThrow { NotFoundException("图片不存在") }
    }

    override fun listByPixivId(pixivId: String): List<PixivWorkDetail> {
        return pixivWorkDetailDao.findAllByPixivId(pixivId)
    }

    override fun listByDownload(download: Boolean): List<PixivWorkDetail> {
        return pixivWorkDetailDao.findAllByDownload(download)
    }

    override fun saveAll(pixivWorkDetailList: List<PixivWorkDetail>): List<PixivWorkDetail> {
        return pixivWorkDetailDao.saveAll(pixivWorkDetailList)
    }

}