package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.Complaint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ComplaintDao : JpaRepository<Complaint, Int>, JpaSpecificationExecutor<Complaint> {
}