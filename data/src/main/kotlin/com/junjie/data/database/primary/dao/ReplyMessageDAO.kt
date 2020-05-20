package com.junjie.data.database.primary.dao

import com.junjie.data.database.primary.entity.ReplyMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ReplyMessageDAO : JpaRepository<ReplyMessage, Int>, JpaSpecificationExecutor<ReplyMessage> {
    fun findAllByCriticIdOrderByCreateDateDesc(criticId: Int): List<ReplyMessage>

    fun countByCriticIdAndReview(criticId: Int, review: Boolean): Long

    fun findAllByCriticIdAndReviewOrderByCreateDateDesc(criticId: Int, review: Boolean): List<ReplyMessage>
}