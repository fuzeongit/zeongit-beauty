package com.zeongit.web.serviceimpl

import com.zeongit.data.constant.CollectState
import com.zeongit.data.database.primary.dao.CollectionDao
import com.zeongit.data.database.primary.entity.Collection
import com.zeongit.web.service.CollectionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class CollectionServiceImpl(private val collectionDao: CollectionDao) : CollectionService {
    override fun exists(userId: Int?, pictureId: Int): CollectState {
        return if (userId == null) {
            CollectState.STRANGE
        } else {
            if (collectionDao.existsByCreatedByAndPictureId(userId, pictureId)) CollectState.CONCERNED else CollectState.STRANGE
        }
    }

    override fun save(userId: Int, pictureId: Int): Collection {
        return collectionDao.save(Collection(pictureId))
    }

    override fun remove(userId: Int, pictureId: Int): Boolean {
        return try {
            collectionDao.deleteByCreatedByAndPictureId(userId, pictureId)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override fun countByPictureId(pictureId: Int): Long {
        return collectionDao.countByPictureId(pictureId)
    }

    override fun pagingByUserId(userId: Int, pageable: Pageable): Page<Collection> {
        return collectionDao.findAllByCreatedBy(userId, pageable)
    }

    override fun pagingByPictureId(pictureId: Int, pageable: Pageable): Page<Collection> {
        return collectionDao.findAllByPictureId(pictureId, pageable)
    }
}