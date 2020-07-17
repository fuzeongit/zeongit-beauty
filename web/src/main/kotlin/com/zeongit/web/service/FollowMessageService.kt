package com.zeongit.web.service

import com.zeongit.data.database.primary.entity.FollowMessage

interface FollowMessageService {
    fun save(followMessage: FollowMessage): FollowMessage

    fun list(followingId: Int): List<FollowMessage>

    fun countUnread(followingId: Int): Long

    fun listUnread(followingId: Int): List<FollowMessage>

    fun deleteByMonthAgo()
}