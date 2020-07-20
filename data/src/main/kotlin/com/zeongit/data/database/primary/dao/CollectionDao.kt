package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.Collection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface CollectionDao : JpaRepository<Collection, Int>, JpaSpecificationExecutor<Collection> {
    fun existsByCreatedByAndPictureId(createdBy: Int, pictureId: Int): Boolean

    @Transactional
    fun deleteByCreatedByAndPictureId(createdBy: Int, pictureId: Int)

    fun countByPictureId(pictureId: Int): Long
}