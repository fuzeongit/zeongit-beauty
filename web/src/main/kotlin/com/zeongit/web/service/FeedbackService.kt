package com.zeongit.web.service

import com.zeongit.data.database.primary.entity.Feedback
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface FeedbackService {
    fun get(id: Int): Feedback

    fun save(feedback: Feedback): Feedback

    fun paging(pageable: Pageable, email: String? = null): Page<Feedback>
}