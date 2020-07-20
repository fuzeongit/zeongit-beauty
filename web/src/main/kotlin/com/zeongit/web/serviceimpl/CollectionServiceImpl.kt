package com.zeongit.web.serviceimpl

import com.zeongit.data.constant.CollectState
import com.zeongit.data.database.primary.dao.CollectionDao
import com.zeongit.data.database.primary.entity.Collection
import com.zeongit.data.database.primary.entity.Picture
import com.zeongit.share.util.DateUtil
import com.zeongit.web.service.CollectionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate


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

    override fun paging(pageable: Pageable, userId: Int, startDate: Date?, endDate: Date?): Page<Collection> {
        return collectionDao.findAll(getSpecification(userId, startDate, endDate), pageable)
    }

    override fun pagingByPictureId(pictureId: Int, pageable: Pageable): Page<Collection> {
        return collectionDao.findAllByPictureId(pictureId, pageable)
    }

    private fun getSpecification(userId: Int, startDate: Date?, endDate: Date?)
            : Specification<Collection> {
        return Specification<Collection> { root, _, criteriaBuilder ->
            val predicatesList = ArrayList<Predicate>()
            predicatesList.add(criteriaBuilder.equal(root.get<Int>("createdBy"), userId))
            if (startDate != null) {
                predicatesList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), DateUtil.getDayBeginTime(startDate)))
            }
            if (endDate != null) {
                predicatesList.add(criteriaBuilder.lessThanOrEqualTo(root.get("createDate"), DateUtil.getDayEndTime(endDate)))
            }
            criteriaBuilder.and(*predicatesList.toArray(arrayOfNulls<Predicate>(predicatesList.size)))
        }
    }
}