package com.junjie.web.serviceimpl

import com.junjie.core.util.DateUtil
import com.junjie.data.database.primary.dao.FollowMessageDao
import com.junjie.data.database.primary.entity.FollowMessage
import com.junjie.web.service.FollowMessageService
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate

@Service
class FollowMessageServiceImpl(private val followMessageDao: FollowMessageDao) : FollowMessageService {
    override fun save(followMessage: FollowMessage): FollowMessage {
        return followMessageDao.save(followMessage)
    }

    override fun list(followingId: Int): List<FollowMessage> {
        return followMessageDao.findAllByFollowingIdOrderByCreateDateDesc(followingId)
    }

    override fun countUnread(followingId: Int): Long {
        return followMessageDao.countByFollowingIdAndReview(followingId, false)
    }

    override fun listUnread(followingId: Int): List<FollowMessage> {
        return followMessageDao.findAllByFollowingIdAndReviewOrderByCreateDateDesc(followingId, false)
    }

    override fun deleteByMonthAgo() {
        val specification = Specification<FollowMessage> { root, _, criteriaBuilder ->
            val predicatesList = ArrayList<Predicate>()
            predicatesList.add(criteriaBuilder.lessThan(root.get("createDate"), DateUtil.addDate(Date(), 0, -30, 0, 0, 0, 0, 0)))
            criteriaBuilder.and(*predicatesList.toArray(arrayOfNulls<Predicate>(predicatesList.size)))
        }
        val list = followMessageDao.findAll(specification)
        for (item in list) {
            followMessageDao.delete(item)
        }
        return
    }

}