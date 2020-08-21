package com.zeongit.web.serviceimpl

import com.zeongit.data.constant.ReadState
import com.zeongit.data.database.primary.dao.ComplaintDao
import com.zeongit.data.database.primary.entity.Complaint
import com.zeongit.share.exception.NotFoundException
import com.zeongit.web.service.ComplaintService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate

@Service
class ComplaintServiceImpl(private val complaintDao: ComplaintDao) : ComplaintService {
    override fun get(id: Int): Complaint {
        return complaintDao.findById(id).orElseThrow { NotFoundException("举报信息不存在") }
    }

    override fun save(complaint: Complaint): Complaint {
        return complaintDao.save(complaint)
    }

    override fun exists(userId: Int?, pictureId: Int?, state: ReadState?): Boolean {
        return complaintDao.count(getSpecification(userId, pictureId, state)) > 0
    }

    override fun paging(pageable: Pageable, userId: Int?, pictureId: Int?, state: ReadState?): Page<Complaint> {
        return complaintDao.findAll(getSpecification(userId, pictureId, state), pageable)
    }

    private fun getSpecification(userId: Int?, pictureId: Int?, state: ReadState?): Specification<Complaint> {
        return Specification<Complaint> { root, _, criteriaBuilder ->
            val predicatesList = ArrayList<Predicate>()
            if (userId != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<Int>("createdBy"), userId))
            }
            if (pictureId != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<Int>("pictureId"), pictureId))
            }
            if (state != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<String>("state"), state))
            }
            criteriaBuilder.and(*predicatesList.toArray(arrayOfNulls<Predicate>(predicatesList.size)))
        }
    }
}