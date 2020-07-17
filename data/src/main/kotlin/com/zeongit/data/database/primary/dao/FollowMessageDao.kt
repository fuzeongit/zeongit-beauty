package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.FollowMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface FollowMessageDao : JpaRepository<FollowMessage, Int>, JpaSpecificationExecutor<FollowMessage> {
    fun findAllByFollowingIdOrderByCreateDateDesc(followingId: Int): List<FollowMessage>

    fun countByFollowingIdAndReview(followingId: Int, review: Boolean): Long

    fun findAllByFollowingIdAndReviewOrderByCreateDateDesc(followingId: Int, review: Boolean): List<FollowMessage>
}