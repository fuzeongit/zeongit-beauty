package com.junjie.data.database.primary.dao

import com.junjie.data.database.primary.entity.CommentMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface CommentMessageDAO : JpaRepository<CommentMessage, Int>, JpaSpecificationExecutor<CommentMessage> {
    fun findAllByAuthorIdOrderByCreateDateDesc(authorId: Int): List<CommentMessage>

    fun countByAuthorIdAndReview(authorId: Int, review: Boolean): Long

    fun findAllByAuthorIdAndReviewOrderByCreateDateDesc(authorId: Int, review: Boolean): List<CommentMessage>
}