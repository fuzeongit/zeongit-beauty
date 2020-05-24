package com.junjie.web.serviceimpl

import com.junjie.data.database.primary.dao.ReplyDao
import com.junjie.data.database.primary.entity.Reply
import com.junjie.web.service.ReplyService
import org.springframework.stereotype.Service

@Service
class ReplyServiceImpl(private val replyDao: ReplyDao) : ReplyService {
    override fun save(reply: Reply): Reply {
        return replyDao.save(reply)
    }

    override fun list(commentId: Int): List<Reply> {
        return replyDao.findAllByCommentIdOrderByCreateDateDesc(commentId)
    }
}