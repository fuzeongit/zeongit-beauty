package com.zeongit.web.serviceimpl

import com.zeongit.data.database.primary.dao.FootprintDao
import com.zeongit.data.database.primary.entity.Footprint
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.util.DateUtil
import com.zeongit.web.service.FootprintService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate

@Service
class FootprintServiceImpl(private val footprintDao: FootprintDao) : FootprintService {
    override fun get(userId: Int, pictureId: Int): Footprint {
        return footprintDao.getFirstByCreatedByAndPictureId(userId, pictureId).orElseThrow { NotFoundException("找不到足迹") }
    }

    override fun exists(userId: Int, pictureId: Int): Boolean {
        return footprintDao.existsByCreatedByAndPictureId(userId, pictureId)
    }

    override fun save(pictureId: Int): Footprint {
        return footprintDao.save(Footprint(pictureId))
    }

    override fun update(userId: Int, pictureId: Int): Footprint {
        val footprint = get(userId, pictureId)
        // 比较特殊，因为没有数据发生变化，所以修改时间不会进行更改，所以要手动修改
        footprint.lastModifiedDate = Date()
        return footprintDao.save(footprint)
    }

    override fun remove(userId: Int, pictureId: Int): Boolean {
        return try {
            footprintDao.deleteByCreatedByAndPictureId(userId, pictureId)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override fun countByPictureId(pictureId: Int): Long {
        return footprintDao.countByPictureId(pictureId)
    }

    override fun paging(pageable: Pageable, userId: Int, startDate: Date?, endDate: Date?): Page<Footprint> {
        return footprintDao.findAll(getSpecification(userId = userId, startDate = startDate, endDate = endDate), pageable)
    }

    override fun pagingByPictureId(pageable: Pageable, pictureId: Int, startDate: Date?, endDate: Date?): Page<Footprint> {
        return footprintDao.findAll(getSpecification(pictureId = pictureId, startDate = startDate, endDate = endDate), pageable)
    }

    private fun getSpecification(userId: Int? = null, pictureId: Int? = null, startDate: Date? = null, endDate: Date? = null)
            : Specification<Footprint> {
        return Specification<Footprint> { root, _, criteriaBuilder ->
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