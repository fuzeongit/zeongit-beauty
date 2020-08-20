package com.zeongit.web.serviceimpl

import com.zeongit.data.database.primary.dao.TagBlackHoleDao
import com.zeongit.data.database.primary.entity.TagBlackHole
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.util.DateUtil
import com.zeongit.web.service.TagBlackHoleService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate

@Service
class TagBlackHoleServiceImpl(private val tagBlackHoleDao: TagBlackHoleDao) : TagBlackHoleService {
    override fun paging(pageable: Pageable, userId: Int, startDate: Date?, endDate: Date?): Page<TagBlackHole> {
        return tagBlackHoleDao.findAll(getSpecification(userId, startDate, endDate), pageable)
    }

    override fun list(userId: Int, startDate: Date?, endDate: Date?): List<TagBlackHole> {
        return tagBlackHoleDao.findAll(getSpecification(userId, startDate, endDate))
    }

    override fun save(tag: String): TagBlackHole {
        return tagBlackHoleDao.save(TagBlackHole(tag))
    }

    override fun remove(userId: Int, tag: String): Boolean {
        return try {
            tagBlackHoleDao.deleteByCreatedByAndTag(userId, tag)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override fun get(userId: Int, tag: String): TagBlackHole {
        return tagBlackHoleDao.findByCreatedByAndTag(userId, tag).orElseThrow { NotFoundException("黑名单不存在") }
    }

    override fun exists(userId: Int, tag: String): Boolean {
        return tagBlackHoleDao.existsByCreatedByAndTag(userId, tag)
    }

    override fun listBlacklist(userId: Int?): MutableList<String> {
        val pictureBlacklist = mutableListOf<String>()
        if (userId != null) {
            pictureBlacklist.addAll(
                    list(userId).map { it.tag }
            )

        }
        return pictureBlacklist
    }

    private fun getSpecification(userId: Int, startDate: Date? = null, endDate: Date? = null)
            : Specification<TagBlackHole> {
        return Specification<TagBlackHole> { root, _, criteriaBuilder ->
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