package com.zeongit.data.database.admin.dao

import com.zeongit.data.database.admin.entity.PixivWork
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PixivWorkDao : JpaRepository<PixivWork, Int> {
    fun existsByPixivId(pixivId: String): Boolean

    fun getByPixivId(pixivId: String): Optional<PixivWork>
}