package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.Reply
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReplyDao : JpaRepository<Reply, Int> {
    fun findAllByCommentIdOrderByCreateDateDesc(commentId: Int): List<Reply>
}