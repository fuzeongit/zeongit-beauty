package com.zeongit.web.serviceimpl

import com.zeongit.data.database.primary.dao.PictureBlackHoleDao
import com.zeongit.data.database.primary.entity.PictureBlackHole
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.util.DateUtil
import com.zeongit.web.service.PictureBlackHoleService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate

@Service
class PictureBlackHoleServiceImpl(private val pictureBlackHoleDao: PictureBlackHoleDao) : PictureBlackHoleService {
    override fun paging(pageable: Pageable, userId: Int, startDate: Date?, endDate: Date?): Page<PictureBlackHole> {
        return pictureBlackHoleDao.findAll(getSpecification(userId, startDate, endDate), pageable)
    }

    override fun list(userId: Int, startDate: Date?, endDate: Date?): List<PictureBlackHole> {
        return pictureBlackHoleDao.findAll(getSpecification(userId, startDate, endDate))
    }

    override fun save(targetId: Int): PictureBlackHole {
        return pictureBlackHoleDao.save(PictureBlackHole(targetId))
    }

    override fun remove(userId: Int, targetId: Int): Boolean {
        return try {
            pictureBlackHoleDao.deleteByCreatedByAndTargetId(userId, targetId)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override fun get(userId: Int, targetId: Int): PictureBlackHole {
        return pictureBlackHoleDao.findByCreatedByAndTargetId(userId, userId).orElseThrow { NotFoundException("黑名单不存在") }
    }

    private fun getSpecification(userId: Int, startDate: Date? = null, endDate: Date? = null)
            : Specification<PictureBlackHole> {
        return Specification<PictureBlackHole> { root, _, criteriaBuilder ->
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