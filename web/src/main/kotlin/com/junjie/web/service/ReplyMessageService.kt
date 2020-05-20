package com.junjie.web.service

import com.junjie.data.database.primary.entity.ReplyMessage

interface ReplyMessageService {
    fun save(replyMessage: ReplyMessage): ReplyMessage

    fun list(criticId: Int): List<ReplyMessage>

    fun countUnread(criticId: Int): Long

    fun listUnread(criticId: Int): List<ReplyMessage>

    fun deleteByMonthAgo()
}