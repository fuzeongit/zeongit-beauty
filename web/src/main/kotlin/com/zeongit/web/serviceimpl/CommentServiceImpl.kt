package com.zeongit.web.serviceimpl

import com.zeongit.data.database.primary.dao.CommentDao
import com.zeongit.data.database.primary.entity.Comment
import com.zeongit.web.service.CommentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl(private val commentDao: CommentDao) : CommentService {
    override fun save(comment: Comment): Comment {
        return commentDao.save(comment)
    }

    override fun count(pictureId: Int): Long {
        return commentDao.countByPictureId(pictureId)
    }

    override fun list(pictureId: Int): List<Comment> {
        return commentDao.findAllByPictureIdOrderByCreateDateDesc(pictureId)
    }

    override fun listTop4(pictureId: Int): List<Comment> {
        return commentDao.findAllByPictureId(pictureId, PageRequest.of(0, 4, Sort(Sort.Direction.DESC, "createDate"))).content
    }

    override fun paging(pageable: Pageable, pictureId: Int): Page<Comment> {
        return commentDao.findAllByPictureId(pictureId, pageable)
    }
}