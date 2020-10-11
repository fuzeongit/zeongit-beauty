package com.zeongit.data.database.admin.dao

import com.zeongit.data.database.admin.entity.NsfwLevel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NsfwLevelDao : JpaRepository<NsfwLevel, Int>