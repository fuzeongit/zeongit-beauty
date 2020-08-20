package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.PictureBlackHole
import com.zeongit.data.database.primary.entity.UserBlackHole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface PictureBlackHoleDao : JpaRepository<PictureBlackHole, Int>, JpaSpecificationExecutor<PictureBlackHole> {
    fun findByCreatedByAndTargetId(createdBy: Int, targetId: Int): Optional<PictureBlackHole>

    fun existsByCreatedByAndTargetId(createdBy: Int, targetId: Int): Boolean

    @Transactional
    fun deleteByCreatedByAndTargetId(createdBy: Int, targetId: Int)
}