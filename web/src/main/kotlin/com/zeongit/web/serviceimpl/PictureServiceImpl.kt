package com.zeongit.web.serviceimpl

import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.util.DateUtil
import com.zeongit.data.constant.PictureLifeState
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.constant.SizeType
import com.zeongit.data.database.primary.dao.PictureDao
import com.zeongit.data.database.primary.entity.Picture
import com.zeongit.data.index.primary.document.PictureDocument
import com.zeongit.share.exception.PermissionException
import com.zeongit.web.service.PictureDocumentService
import com.zeongit.web.service.PictureService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.criteria.Predicate


@Service
class PictureServiceImpl(private val pictureDao: PictureDao,
                         private val pictureDocumentService: PictureDocumentService) : PictureService {
    override fun paging(pageable: Pageable, userId: Int?, name: String?, privacy: PrivacyState?, life: PictureLifeState?, master: Boolean?, startDate: Date?, endDate: Date?, sizeType: SizeType?): Page<Picture> {
        val specification = Specification<Picture> { root, _, criteriaBuilder ->
            val predicatesList = ArrayList<Predicate>()
            if (!name.isNullOrEmpty()) {
                predicatesList.add(criteriaBuilder.like(root.get("name"), "%$name%"))
            }
            if (privacy != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<Int>("privacy"), privacy))
            }
            if (life != null) {
                predicatesList.add(criteriaBuilder.equal(root.get<Int>("life"), life))
            }
            if (master != null) {
                if (master) {
                    predicatesList.add(criteriaBuilder.like(root.get("url"), "%_p0%"))
                } else {
                    predicatesList.add(criteriaBuilder.notLike(root.get("url"), "%_p0%"))
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
        return pictureDao.findAll(specification, pageable)
    }

    override fun get(id: Int): Picture {
        return getByLife(id, PictureLifeState.EXIST)
    }

    override fun getSelf(id: Int, userId: Int): Picture {
        val picture = get(id)
        picture.createdBy != userId && throw PermissionException("您无权操作该图片")
        return picture
    }

    override fun getByLife(id: Int, life: PictureLifeState?): Picture {
        if (life != null) {
            return pictureDao.findByIdAndLife(id, life).orElseThrow { NotFoundException("图片不存在") }
        }
        return pictureDao.findById(id).orElseThrow { NotFoundException("图片不存在") }
    }

    override fun hide(picture: Picture): PrivacyState {
        when (picture.privacy) {
            PrivacyState.PRIVATE -> picture.privacy = PrivacyState.PUBLIC
            PrivacyState.PUBLIC -> picture.privacy = PrivacyState.PRIVATE
            else -> picture.privacy
        }
        save(picture)
        return picture.privacy
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
        return pictureDao.deleteById(id)
    }

    override fun list(): List<Picture> {
        return listByLife(PictureLifeState.EXIST)
    }

    override fun listByLife(life: PictureLifeState?): List<Picture> {
        if (life != null) {
            return pictureDao.findAllByLife(life)
        }
        return pictureDao.findAll()
    }

    override fun listByUserId(userId: Int): List<Picture> {
        return listByUserIdAndLife(userId, PictureLifeState.EXIST)
    }

    override fun listByUserIdAndLife(userId: Int, life: PictureLifeState?): List<Picture> {
        if (life != null) {
            return pictureDao.findAllByCreatedByAndLife(userId, life)
        }
        return pictureDao.findAllByCreatedBy(userId)
    }

    override fun save(picture: Picture, force: Boolean): PictureDocument {
        if (picture.life == PictureLifeState.DISAPPEAR && !force) {
            throw NotFoundException("图片不存在")
        }
        return pictureDocumentService.save(PictureDocument(pictureDao.save(picture)))
    }

    override fun synchronizationIndexPicture(): Long {
        return pictureDocumentService.saveAll(list().map {
            PictureDocument(it)
        }).toList().size.toLong()
    }
}