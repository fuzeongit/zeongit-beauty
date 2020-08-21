package com.zeongit.web.service

import com.zeongit.data.constant.ReadState
import com.zeongit.data.database.primary.entity.Complaint
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface ComplaintService {
    fun get(id: Int): Complaint

    fun save(complaint: Complaint): Complaint

    fun exists(userId: Int? = null, pictureId: Int? = null, state: ReadState? = null): Boolean

    fun paging(pageable: Pageable, userId: Int? = null, pictureId: Int? = null, state: ReadState? = null): Page<Complaint>
}