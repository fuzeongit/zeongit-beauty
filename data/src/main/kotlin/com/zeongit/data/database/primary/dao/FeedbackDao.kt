package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.Comment
import com.zeongit.data.database.primary.entity.Feedback
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface FeedbackDao : JpaRepository<Feedback, Int>, JpaSpecificationExecutor<Feedback> {
    fun findAllByEmail(email: String, pageable: Pageable): Page<Feedback>
}