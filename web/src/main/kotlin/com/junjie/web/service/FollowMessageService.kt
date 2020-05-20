package com.junjie.web.service

import com.junjie.data.database.primary.entity.FollowMessage

interface FollowMessageService {
    fun save(followMessage: FollowMessage): FollowMessage

    fun list(followingId: Int): List<FollowMessage>

    fun countUnread(followingId: Int): Long

    fun listUnread(followingId: Int): List<FollowMessage>

    fun deleteByMonthAgo()
}