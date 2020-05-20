package com.junjie.web.serviceimpl

import com.junjie.core.exception.NotFoundException
import com.junjie.data.database.primary.dao.FootprintDAO
import com.junjie.data.database.primary.entity.Footprint
import com.junjie.web.service.FootprintService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class IntFootprintServiceImpl(private val footprintDAO: FootprintDAO) : FootprintService {
    override fun get(userId: Int, pictureId: Int): Footprint {
        return footprintDAO.getFirstByCreatedByAndPictureId(userId, pictureId).orElseThrow { NotFoundException("找不到足迹") }
    }

    override fun exists(userId: Int, pictureId: Int): Boolean {
        return footprintDAO.existsByCreatedByAndPictureId(userId, pictureId)
    }

    override fun save(pictureId: Int): Footprint {
        return footprintDAO.save(Footprint(pictureId))
    }

    override fun update(userId: Int, pictureId: Int): Footprint {
        val footprint = get(userId, pictureId)
        // 比较特殊，因为没有数据发生变化，所以修改时间不会进行更改，所以要手动修改
        footprint.lastModifiedDate = Date()
        return footprintDAO.save(footprint)
    }

    override fun remove(userId: Int, pictureId: Int): Boolean {
        return try {
            footprintDAO.deleteByCreatedByAndPictureId(userId, pictureId)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override fun countByPictureId(pictureId: Int): Long {
        return footprintDAO.countByPictureId(pictureId)
    }

    override fun pagingByUserId(userId: Int, pageable: Pageable): Page<Footprint> {
        return footprintDAO.findAllByCreatedBy(userId, pageable)
    }

    override fun pagingByPictureId(pictureId: Int, pageable: Pageable): Page<Footprint> {
        return footprintDAO.findAllByPictureId(pictureId, pageable)
    }
}