package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.Collection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface CollectionDao : JpaRepository<Collection, Int> {
    fun existsByCreatedByAndPictureId(createdBy: Int, pictureId: Int): Boolean
    @Transactional
    fun deleteByCreatedByAndPictureId(createdBy: Int, pictureId: Int)

    fun countByPictureId(pictureId: Int): Long

    fun findAllByCreatedBy(createdBy: Int, pageable: Pageable): Page<Collection>

    fun findAllByPictureId(pictureId: Int, pageable: Pageable): Page<Collection>
}