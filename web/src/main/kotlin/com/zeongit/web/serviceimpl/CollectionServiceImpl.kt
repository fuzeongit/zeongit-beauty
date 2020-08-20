package com.zeongit.web.serviceimpl

import com.zeongit.data.constant.CollectState
import com.zeongit.data.database.primary.dao.CollectionDao
import com.zeongit.data.database.primary.entity.Collection
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

    override fun save(pictureId: Int): Collection {
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
        return collectionDao.findAll(getSpecification(userId = userId, startDate = startDate, endDate = endDate), pageable)
    }

    override fun pagingByPictureId(pageable: Pageable, pictureId: Int, startDate: Date?, endDate: Date?): Page<Collection> {
        return collectionDao.findAll(getSpecification(pictureId = pictureId, startDate = startDate, endDate = endDate), pageable)
    }

    private fun getSpecification(userId: Int? = null, pictureId: Int? = null, startDate: Date? = null, endDate: Date? = null)
            : Specification<Collection> {
        return Specification<Collection> { root, _, criteriaBuilder ->
            val predicatesList = ArrayList<Predicate>()
            if (userId != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<Int>("createdBy"), userId))
            }
            if (pictureId != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<Int>("pictureId"), pictureId))
            }
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