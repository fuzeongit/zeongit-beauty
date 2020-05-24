package com.junjie.data.database.primary.dao

import com.junjie.data.database.primary.entity.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentDao : JpaRepository<Comment, Int> {
    fun countByPictureId(pictureId: Int): Long

    fun findAllByPictureIdOrderByCreateDateDesc(pictureId: Int): List<Comment>

    fun findAllByPictureId(pictureId: Int, pageable: Pageable): Page<Comment>
}