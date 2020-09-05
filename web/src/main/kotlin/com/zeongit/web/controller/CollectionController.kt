package com.zeongit.web.controller

import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.exception.PermissionException
import com.zeongit.share.exception.ProgramException
import com.zeongit.share.exception.SignInException
import com.zeongit.data.constant.CollectState
import com.zeongit.data.constant.PrivacyState
import com.zeongit.web.core.communal.PictureVoAbstract
import com.zeongit.web.service.CollectionService
import com.zeongit.web.service.FollowService
import com.zeongit.web.service.PictureDocumentService
import com.zeongit.web.service.UserInfoService
import com.zeongit.web.vo.CollectionPictureVo
import com.zeongit.web.vo.UserInfoVo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author fjj
 * 图片收藏的
 */
@RestController
@RequestMapping("collection")
class CollectionController(override val pictureDocumentService: PictureDocumentService,
                           override val collectionService: CollectionService,
                           override val userInfoService: UserInfoService,
                           override val followService: FollowService) : PictureVoAbstract() {
    class FocusDto {
        var pictureId: Int = 0
    }

    class UnFocusDto {
        var pictureIdList: List<Int>? = null
            get() {
                if (field == null || field!!.isEmpty()) {
                    throw ProgramException("请选择一张图片")
                }
                return field
            }
    }

    @Auth
    @PostMapping("focus")
    @RestfulPack
    fun focus(@CurrentUserId userId: Int, @RequestBody dto: FocusDto): CollectState {
        val pictureId = dto.pictureId
        val picture = try {
            pictureDocumentService.get(pictureId)
        } catch (e: NotFoundException) {
            null
        }
        return if (picture == null) {
            //关注了但图片为空立即取消关注
            if (collectionService.exists(userId, pictureId) == CollectState.CONCERNED) {
                collectionService.remove(userId, pictureId)
                CollectState.STRANGE
            } else {
                throw PermissionException("操作有误")
            }
        } else {
            picture.createdBy == userId && throw ProgramException("不能收藏自己的作品")
            val flag = if (collectionService.exists(userId, pictureId) == CollectState.STRANGE) {
                picture.privacy == PrivacyState.PRIVATE && throw PermissionException("不能收藏私密图片")
                collectionService.save(pictureId)
                CollectState.CONCERNED
            } else {
                collectionService.remove(userId, pictureId)
                CollectState.STRANGE
            }
            pictureDocumentService.saveLikeAmount(picture, collectionService.countByPictureId(pictureId))
            flag
        }
    }

    /**
     * 取消收藏一组
     */
    @Auth
    @PostMapping("unFocus")
    @RestfulPack
    fun unFocus(@CurrentUserId userId: Int, @RequestBody dto: UnFocusDto): List<Int> {
        val pictureIdList = dto.pictureIdList!!
        val newPictureIdList = mutableListOf<Int>()
        for (pictureId in pictureIdList) {
            if (collectionService.exists(userId, pictureId) != CollectState.CONCERNED) {
                continue
            }
            val picture = try {
                pictureDocumentService.get(pictureId)
            } catch (e: NotFoundException) {
                null
            }
            collectionService.remove(userId, pictureId)
            picture?.let {
                pictureDocumentService.saveLikeAmount(it, collectionService.countByPictureId(pictureId))
            }
            newPictureIdList.add(pictureId)
        }
        return newPictureIdList
    }

    /**
     * 获取列表
     */
    @GetMapping("paging")
    @RestfulPack
    fun paging(@CurrentUserId userId: Int?, @PageableDefault(value = 20) pageable: Pageable, targetId: Int?, startDate: Date?, endDate: Date?): Page<CollectionPictureVo> {
        (userId == null && targetId == null) && throw SignInException("请重新登录")
        val page = collectionService.paging(pageable, targetId ?: userId!!, startDate, endDate)
        val collectionPictureVoList = ArrayList<CollectionPictureVo>()
        for (collection in page.content) {
            val collectionPictureVo = try {
                val picture = pictureDocumentService.get(collection.pictureId)
                //图片被隐藏
                if (picture.privacy == PrivacyState.PRIVATE) {
                    picture.url = ""
                }
                CollectionPictureVo(
                        picture,
                        getPictureVo(picture, userId).focus,
                        collection.lastModifiedDate!!,
                        getUserVo(picture.createdBy, userId))
            } catch (e: NotFoundException) {
                CollectionPictureVo(collection.pictureId, collectionService.exists(targetId, collection.pictureId), collection.lastModifiedDate!!)
            } catch (e: PermissionException) {
                CollectionPictureVo(collection.pictureId, collectionService.exists(targetId, collection.pictureId), collection.lastModifiedDate!!)
            }
            collectionPictureVoList.add(collectionPictureVo)
        }
        return PageImpl(collectionPictureVoList, page.pageable, page.totalElements)
    }

    @GetMapping("pagingUser")
    @RestfulPack
    fun pagingUser(@CurrentUserId userId: Int?, @PageableDefault(value = 20) pageable: Pageable, pictureId: Int): Page<UserInfoVo> {
        val page = collectionService.pagingByPictureId(pageable, pictureId)
        val userVoList = page.content.map {
            getUserVo(it.createdBy!!, userId)
        }
        return PageImpl(userVoList, page.pageable, page.totalElements)
    }
}