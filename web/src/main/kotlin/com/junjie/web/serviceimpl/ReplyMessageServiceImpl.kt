package com.junjie.web.serviceimpl

import com.junjie.core.util.DateUtil
import com.junjie.data.database.primary.dao.ReplyMessageDAO
import com.junjie.data.database.primary.entity.ReplyMessage
import com.junjie.web.service.ReplyMessageService
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate

@Service
class ReplyMessageServiceImpl(private val replyMessageDAO: ReplyMessageDAO) : ReplyMessageService {
    override fun save(replyMessage: ReplyMessage): ReplyMessage {
        return replyMessageDAO.save(replyMessage)
    }

    override fun list(criticId: Int): List<ReplyMessage> {
        return replyMessageDAO.findAllByCriticIdOrderByCreateDateDesc(criticId)
    }

    override fun countUnread(criticId: Int): Long {
        return replyMessageDAO.countByCriticIdAndReview(criticId, false)
    }

    override fun listUnread(criticId: Int): List<ReplyMessage> {
        return replyMessageDAO.findAllByCriticIdAndReviewOrderByCreateDateDesc(criticId, false)
    }

    override fun deleteByMonthAgo() {
        val specification = Specification<ReplyMessage> { root, _, criteriaBuilder ->
            val predicatesList = ArrayList<Predicate>()
            predicatesList.add(criteriaBuilder.greaterThan(root.get("createDate"), DateUtil.addDate(Date(), 0, -30, 0, 0, 0, 0, 0)))
            criteriaBuilder.and(*predicatesList.toArray(arrayOfNulls<Predicate>(predicatesList.size)))
        }
        val list = replyMessageDAO.findAll(specification)
        for (item in list) {
            replyMessageDAO.delete(item)
        }
        return
    }
}