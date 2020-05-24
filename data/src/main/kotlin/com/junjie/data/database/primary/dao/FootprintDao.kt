package com.junjie.data.database.primary.dao

import com.junjie.data.database.primary.entity.Footprint
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface FootprintDao : JpaRepository<Footprint, Int>, JpaSpecificationExecutor<Footprint> {
    fun getFirstByCreatedByAndPictureId(createdBy: Int, pictureId: Int): Optional<Footprint>

    fun existsByCreatedByAndPictureId(createdBy: Int, pictureId: Int): Boolean

    @Transactional
    fun deleteByCreatedByAndPictureId(createdBy: Int, pictureId: Int)

    fun countByPictureId(pictureId: Int): Long

    fun findAllByCreatedBy(createdBy: Int, pageable: Pageable): Page<Footprint>

    fun findAllByPictureId(pictureId: Int, pageable: Pageable): Page<Footprint>
}