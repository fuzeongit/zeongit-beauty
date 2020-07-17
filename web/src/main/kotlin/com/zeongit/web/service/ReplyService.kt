package com.zeongit.web.service

import com.zeongit.data.database.primary.entity.Reply

/**
 * 回复的服务
 *
 * @author fjj
 */
interface ReplyService {
    fun save(reply: Reply): Reply

    fun list(commentId: Int): List<Reply>
}