package com.junjie.web.service

import com.junjie.data.database.primary.entity.Reply

/**
 * 回复的服务
 *
 * @author fjj
 */
interface ReplyService {
    fun save(reply: Reply): Reply

    fun list(commentId: Int): List<Reply>
}