package com.junjie.web.serviceimpl

import com.junjie.data.constant.CollectState
import com.junjie.data.database.primary.dao.CollectionDAO
import com.junjie.data.database.primary.entity.Collection
import com.junjie.web.service.CollectionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class CollectionServiceImpl(private val collectionDAO: CollectionDAO) : CollectionService {
    override fun exists(userId: Int?, pictureId: Int): CollectState {
        return if (userId == null) {
            CollectState.CONCERNED
        } else {
            if (collectionDAO.existsByCreatedByAndPictureId(userId, pictureId)) CollectState.CONCERNED else CollectState.STRANGE
        }
    }

    override fun save(userId: Int, pictureId: Int): Collection {
        return collectionDAO.save(Collection(pictureId))
    }

    override fun remove(userId: Int, pictureId: Int): Boolean {
        return try {
            collectionDAO.deleteByCreatedByAndPictureId(userId, pictureId)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override fun countByPictureId(pictureId: Int): Long {
        return collectionDAO.countByPictureId(pictureId)
    }

    override fun pagingByUserId(userId: Int, pageable: Pageable): Page<Collection> {
        return collectionDAO.findAllByCreatedBy(userId, pageable)
    }

    override fun pagingByPictureId(pictureId: Int, pageable: Pageable): Page<Collection> {
        return collectionDAO.findAllByPictureId(pictureId, pageable)
    }
}