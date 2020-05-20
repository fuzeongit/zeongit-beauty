package com.junjie.web.serviceimpl

import com.junjie.core.exception.NotFoundException
import com.junjie.core.util.DateUtil
import com.junjie.data.constant.PictureLifeState
import com.junjie.data.constant.PrivacyState
import com.junjie.data.constant.SizeType
import com.junjie.data.database.primary.dao.PictureDAO
import com.junjie.data.database.primary.entity.Picture
import com.junjie.data.index.primary.document.PictureDocument
import com.junjie.web.service.PictureDocumentService
import com.junjie.web.service.PictureService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate


@Service
class PictureServiceImpl(private val pictureDAO: PictureDAO,
                         private val pictureDocumentService: PictureDocumentService) : PictureService {
    override fun paging(pageable: Pageable, userId: Int?, name: String?, privacy: PrivacyState?, life: PictureLifeState?, master: Boolean?, startDate: Date?, endDate: Date?, sizeType: SizeType?): Page<Picture> {
        val specification = Specification<Picture> { root, _, criteriaBuilder ->
            val predicatesList = ArrayList<Predicate>()
            if (!name.isNullOrEmpty()) {
                predicatesList.add(criteriaBuilder.like(root.get<String>("name"), "%$name%"))
            }
            if (privacy != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<Int>("privacy"), privacy))
            }
            if (life != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<Int>("life"), life))
            }
            if (master != null) {
                if (master) {
                    predicatesList.add(criteriaBuilder.like(root.get<String>("url"), "%_p0%"))
                } else {
                    predicatesList.add(criteriaBuilder.notLike(root.get<String>("url"), "%_p0%"))
                }
            }
            if (startDate != null) {
                predicatesList.add(criteriaBuilder.greaterThan(root.get("createDate"), DateUtil.getDayBeginTime(startDate)))
            }
            if (endDate != null) {
                predicatesList.add(criteriaBuilder.lessThan(root.get("createDate"), DateUtil.getDayEndTime(endDate)))
            }
            if (userId != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<Int>("createdBy"), userId))
            }
            if (sizeType != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<String>("sizeType"), sizeType))
            }
            criteriaBuilder.and(*predicatesList.toArray(arrayOfNulls<Predicate>(predicatesList.size)))
        }
        return pictureDAO.findAll(specification, pageable)
    }

    override fun get(id: Int): Picture {
        return getByLife(id, PictureLifeState.EXIST)
    }

    override fun getByLife(id: Int, life: PictureLifeState?): Picture {
        if (life != null) {
            return pictureDAO.findByIdAndLife(id, life).orElseThrow { NotFoundException("图片不存在") }
        }
        return pictureDAO.findById(id).orElseThrow { NotFoundException("图片不存在") }
    }

    override fun remove(picture: Picture): Boolean {
        return try {
            picture.life = PictureLifeState.DISAPPEAR
            save(picture, true)
            //删除索引
            pictureDocumentService.remove(picture.id!!)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override fun reduction(id: Int): PictureDocument {
        val picture = getByLife(id)
        picture.life = PictureLifeState.EXIST
        return save(picture, true)
    }

    override fun delete(id: Int) {
        return pictureDAO.deleteById(id)
    }

    override fun list(): List<Picture> {
        return listByLife(PictureLifeState.EXIST)
    }

    override fun listByLife(life: PictureLifeState?): List<Picture> {
        if (life != null) {
            return pictureDAO.findAllByLife(life)
        }
        return pictureDAO.findAll()
    }

    override fun listByUserId(userId: Int): List<Picture> {
        return listByUserIdAndLife(userId, PictureLifeState.EXIST)
    }

    override fun listByUserIdAndLife(userId: Int, life: PictureLifeState?): List<Picture> {
        if (life != null) {
            return pictureDAO.findAllByCreatedByAndLife(userId, life)
        }
        return pictureDAO.findAllByCreatedBy(userId)
    }

    override fun save(picture: Picture, force: Boolean): PictureDocument {
        if (picture.life == PictureLifeState.DISAPPEAR && !force) {
            throw NotFoundException("图片不存在")
        }
        return pictureDocumentService.save(PictureDocument(pictureDAO.save(picture)))
    }

    override fun synchronizationIndexPicture(): Long {
        return pictureDocumentService.saveAll(list().map {
            PictureDocument(it)
        }).toList().size.toLong()
    }
}