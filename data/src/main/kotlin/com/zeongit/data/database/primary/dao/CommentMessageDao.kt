package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.CommentMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface CommentMessageDao : JpaRepository<CommentMessage, Int>, JpaSpecificationExecutor<CommentMessage> {
    fun findAllByAuthorIdOrderByCreateDateDesc(authorId: Int): List<CommentMessage>

    fun countByAuthorIdAndReview(authorId: Int, review: Boolean): Long

    fun findAllByAuthorIdAndReviewOrderByCreateDateDesc(authorId: Int, review: Boolean): List<CommentMessage>
}