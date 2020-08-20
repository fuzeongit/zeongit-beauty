package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.UserBlackHole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface UserBlackHoleDao : JpaRepository<UserBlackHole, Int>, JpaSpecificationExecutor<UserBlackHole> {
    fun findByCreatedByAndTargetId(createdBy: Int, targetId: Int): Optional<UserBlackHole>

    @Transactional
    fun deleteByCreatedByAndTargetId(createdBy: Int, targetId: Int)
}