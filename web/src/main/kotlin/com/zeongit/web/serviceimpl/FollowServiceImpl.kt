package com.zeongit.web.serviceimpl

import com.zeongit.data.constant.FollowState
import com.zeongit.data.database.primary.dao.FollowDao
import com.zeongit.data.database.primary.entity.Collection
import com.zeongit.data.database.primary.entity.Follow
import com.zeongit.share.util.DateUtil
import com.zeongit.web.service.FollowService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate

@Service
class FollowServiceImpl(private val followDao: FollowDao) : FollowService {
    override fun exists(followerId: Int?, followingId: Int): FollowState {
        if (followerId == null) {
            return FollowState.STRANGE
        }
        if (followerId == followingId) {
            return FollowState.SElF
        }
        return try {
            if (followDao.existsByCreatedByAndFollowingId(followerId, followingId)) FollowState.CONCERNED else FollowState.STRANGE
        } catch (e: Exception) {
            FollowState.SElF
        }
    }

    override fun save(followingId: Int): Follow {
        val follow = Follow(followingId)
        return followDao.save(follow)
    }

    override fun remove(followerId: Int, followingId: Int): Boolean {
        return try {
            followDao.deleteByCreatedByAndFollowingId(followerId, followingId)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override fun pagingByFollowerId(pageable: Pageable, followerId: Int): Page<Follow> {
        return followDao.findAll(getSpecification(followerId = followerId), pageable)
    }

    override fun pagingByFollowingId(pageable: Pageable, followingId: Int): Page<Follow> {
        return followDao.findAll(getSpecification(followingId = followingId), pageable)
    }

    override fun listByFollowerId(followerId: Int): List<Follow> {
        return followDao.findAll(getSpecification(followerId = followerId))
    }

    private fun getSpecification(followerId: Int? = null, followingId: Int? = null, startDate: Date? = null, endDate: Date? = null)
            : Specification<Follow> {
        return Specification<Follow> { root, _, criteriaBuilder ->
            val predicatesList = ArrayList<Predicate>()
            if (followerId != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<Int>("createdBy"), followerId))
            }
            if (followingId != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<Int>("followingId"), followingId))
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
