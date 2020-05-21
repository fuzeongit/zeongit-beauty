package com.junjie.web.serviceimpl

import com.junjie.data.database.primary.dao.CommentDAO
import com.junjie.data.database.primary.entity.Comment
import com.junjie.web.service.CommentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl(private val commentDAO: CommentDAO) : CommentService {
    override fun save(comment: Comment): Comment {
        return commentDAO.save(comment)
    }

    override fun count(pictureId: Int): Long {
        return commentDAO.countByPictureId(pictureId)
    }

    override fun list(pictureId: Int): List<Comment> {
        return commentDAO.findAllByPictureIdOrderByCreateDateDesc(pictureId)
    }

    override fun listTop4(pictureId: Int): List<Comment> {
        return commentDAO.findAllByPictureId(pictureId, PageRequest.of(0, 4, Sort(Sort.Direction.DESC, "createDate"))).content
    }

    override fun paging(pictureId: Int, pageable: Pageable): Page<Comment> {
        return commentDAO.findAllByPictureId(pictureId, pageable)
    }
}