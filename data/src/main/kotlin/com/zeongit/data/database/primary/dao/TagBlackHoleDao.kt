package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.PictureBlackHole
import com.zeongit.data.database.primary.entity.TagBlackHole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface TagBlackHoleDao : JpaRepository<TagBlackHole, Int>, JpaSpecificationExecutor<TagBlackHole> {
    fun findByCreatedByAndTag(createdBy: Int, tag: String): Optional<TagBlackHole>

    fun existsByCreatedByAndTag(createdBy: Int, tag: String): Boolean

    @Transactional
    fun deleteByCreatedByAndTag(createdBy: Int, tag: String)
}