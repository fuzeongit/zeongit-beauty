package com.junjie.data.database.primary.dao

import com.junjie.data.database.primary.entity.Reply
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReplyDao : JpaRepository<Reply, Int> {
    fun findAllByCommentIdOrderByCreateDateDesc(commentId: Int): List<Reply>
}