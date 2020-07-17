package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.ReplyMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ReplyMessageDao : JpaRepository<ReplyMessage, Int>, JpaSpecificationExecutor<ReplyMessage> {
    fun findAllByCriticIdOrderByCreateDateDesc(criticId: Int): List<ReplyMessage>

    fun countByCriticIdAndReview(criticId: Int, review: Boolean): Long

    fun findAllByCriticIdAndReviewOrderByCreateDateDesc(criticId: Int, review: Boolean): List<ReplyMessage>
}