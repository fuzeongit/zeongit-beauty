package com.junjie.web.service

import com.junjie.data.database.primary.entity.CommentMessage

interface CommentMessageService {
    fun save(commentMessage: CommentMessage): CommentMessage

    fun list(authorId: Int): List<CommentMessage>

    fun countUnread(authorId: Int): Long

    fun listUnread(authorId: Int): List<CommentMessage>

    fun deleteByMonthAgo()
}