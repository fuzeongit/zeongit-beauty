package com.zeongit.web.serviceimpl

import com.zeongit.data.database.primary.dao.UserBlackHoleDao
import com.zeongit.data.database.primary.entity.UserBlackHole
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.util.DateUtil
import com.zeongit.web.service.UserBlackHoleService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate

@Service
class UserBlackHoleServiceImpl(private val userBlackHoleDao: UserBlackHoleDao) : UserBlackHoleService {
    override fun paging(pageable: Pageable, userId: Int, startDate: Date?, endDate: Date?): Page<UserBlackHole> {
        return userBlackHoleDao.findAll(getSpecification(userId, startDate, endDate), pageable)
    }

    override fun list(userId: Int, startDate: Date?, endDate: Date?): List<UserBlackHole> {
        return userBlackHoleDao.findAll(getSpecification(userId, startDate, endDate))
    }

    override fun save(targetId: Int): UserBlackHole {
        return userBlackHoleDao.save(UserBlackHole(targetId))
    }

    override fun remove(userId: Int, targetId: Int): Boolean {
        return try {
            userBlackHoleDao.deleteByCreatedByAndTargetId(userId, targetId)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override fun get(userId: Int, targetId: Int): UserBlackHole {
        return userBlackHoleDao.findByCreatedByAndTargetId(userId, userId).orElseThrow { NotFoundException("黑名单不存在") }
    }

    override fun exists(userId: Int, targetId: Int): Boolean {
        return userBlackHoleDao.existsByCreatedByAndTargetId(userId, targetId)
    }

    override fun listBlacklist(userId: Int?): MutableList<Int> {
        val userBlacklist = mutableListOf<Int>()
        if (userId != null) {
            userBlacklist.addAll(
                    list(userId).map { it.targetId }
            )
        }
        return userBlacklist
    }

    private fun getSpecification(userId: Int, startDate: Date? = null, endDate: Date? = null)
            : Specification<UserBlackHole> {
        return Specification<UserBlackHole> { root, _, criteriaBuilder ->
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