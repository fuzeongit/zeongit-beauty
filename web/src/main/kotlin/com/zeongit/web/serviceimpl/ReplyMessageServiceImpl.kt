package com.zeongit.web.serviceimpl

import com.zeongit.share.util.DateUtil
import com.zeongit.data.database.primary.dao.ReplyMessageDao
import com.zeongit.data.database.primary.entity.ReplyMessage
import com.zeongit.web.service.ReplyMessageService
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate

@Service
class ReplyMessageServiceImpl(private val replyMessageDao: ReplyMessageDao) : ReplyMessageService {
    override fun save(replyMessage: ReplyMessage): ReplyMessage {
        return replyMessageDao.save(replyMessage)
    }

    override fun list(criticId: Int): List<ReplyMessage> {
        return replyMessageDao.findAllByCriticIdOrderByCreateDateDesc(criticId)
    }

    override fun countUnread(criticId: Int): Long {
        return replyMessageDao.countByCriticIdAndReview(criticId, false)
    }

    override fun listUnread(criticId: Int): List<ReplyMessage> {
        return replyMessageDao.findAllByCriticIdAndReviewOrderByCreateDateDesc(criticId, false)
    }

    override fun deleteByMonthAgo() {
        val specification = Specification<ReplyMessage> { root, _, criteriaBuilder ->
            val predicatesList = ArrayList<Predicate>()
            predicatesList.add(criteriaBuilder.greaterThan(root.get("createDate"), DateUtil.addDate(Date(), 0, -30, 0, 0, 0, 0, 0)))
            criteriaBuilder.and(*predicatesList.toArray(arrayOfNulls<Predicate>(predicatesList.size)))
        }
        val list = replyMessageDao.findAll(specification)
        for (item in list) {
            replyMessageDao.delete(item)
        }
        return
    }
}