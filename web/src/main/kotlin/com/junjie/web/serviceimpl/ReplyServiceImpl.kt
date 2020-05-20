package com.junjie.web.serviceimpl

import com.junjie.data.database.primary.dao.ReplyDAO
import com.junjie.data.database.primary.entity.Reply
import com.junjie.web.service.ReplyService
import org.springframework.stereotype.Service

@Service
class ReplyServiceImpl(private val replyDAO: ReplyDAO) : ReplyService {
    override fun save(reply: Reply): Reply {
        return replyDAO.save(reply)
    }

    override fun list(commentId: Int): List<Reply> {
        return replyDAO.findAllByCommentIdOrderByCreateDateDesc(commentId)
    }
}