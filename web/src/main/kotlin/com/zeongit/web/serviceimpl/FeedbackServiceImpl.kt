package com.zeongit.web.serviceimpl

import com.zeongit.data.constant.ReadState
import com.zeongit.share.exception.NotFoundException
import com.zeongit.data.database.primary.dao.FeedbackDao
import com.zeongit.data.database.primary.entity.Collection
import com.zeongit.data.database.primary.entity.Feedback
import com.zeongit.share.util.DateUtil
import com.zeongit.web.service.FeedbackService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.ArrayList
import javax.persistence.criteria.Predicate

@Service
class FeedbackServiceImpl(private val feedbackDao: FeedbackDao) : FeedbackService {
    override fun get(id: Int): Feedback {
        return feedbackDao.findById(id).orElseThrow { NotFoundException("反馈信息不存在") }
    }

    override fun save(feedback: Feedback): Feedback {
        return feedbackDao.save(feedback)
    }

    override fun paging(pageable: Pageable, email: String?, state: ReadState?): Page<Feedback> {
        return feedbackDao.findAll({ root, _, criteriaBuilder ->
            val predicatesList = ArrayList<Predicate>()
            if (email != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<String>("email"), email))
            }
            if (state != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<String>("state"), state))
            }
            criteriaBuilder.and(*predicatesList.toArray(arrayOfNulls<Predicate>(predicatesList.size)))
        }, pageable)
    }
}