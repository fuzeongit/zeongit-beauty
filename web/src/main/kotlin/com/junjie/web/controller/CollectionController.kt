package com.junjie.web.controller

import com.junjie.core.annotations.Auth
import com.junjie.core.annotations.CurrentUserInfoId
import com.junjie.core.annotations.RestfulPack
import com.junjie.core.exception.NotFoundException
import com.junjie.core.exception.PermissionException
import com.junjie.core.exception.ProgramException
import com.junjie.core.exception.SignInException
import com.junjie.data.constant.CollectState
import com.junjie.data.constant.PrivacyState
import com.junjie.share.service.UserInfoService
import com.junjie.web.core.communal.PictureVoAbstract
import com.junjie.web.service.CollectionService
import com.junjie.web.service.FollowService
import com.junjie.web.service.PictureDocumentService
import com.junjie.web.vo.CollectionPictureVo
import com.junjie.web.vo.UserInfoVo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

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

    class UnFocusDto{
        var pictureIdList:List<Int>? = null
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
    fun focus(@CurrentUserInfoId userId: Int, @RequestBody dto: FocusDto): CollectState {
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
                throw PermissionException("不能收藏已移除图片")
            }
        } else {
            picture.createdBy == userId && throw ProgramException("不能收藏自己的作品")
            val flag = if (collectionService.exists(userId, pictureId) == CollectState.STRANGE) {
                picture.privacy == PrivacyState.PRIVATE && throw PermissionException("不能收藏私密图片")
                collectionService.save(userId, pictureId)
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
    fun unFocus(@CurrentUserInfoId userId: Int, @RequestBody dto: UnFocusDto): List<Int> {
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
    fun paging(@CurrentUserInfoId userId: Int?, targetId: Int?, @PageableDefault(value = 20) pageable: Pageable): Page<CollectionPictureVo> {
        (userId == null && targetId == null) && throw SignInException("请重新登录")
        val page = collectionService.pagingByUserId(targetId ?: userId!!, pageable)
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
    fun pagingUser(@CurrentUserInfoId userId: Int?, pictureId: Int, @PageableDefault(value = 20) pageable: Pageable): Page<UserInfoVo> {
        val page = collectionService.pagingByPictureId(pictureId, pageable)
        val userVoList = page.content.map {
            getUserVo(it.createdBy!!, userId)
        }
        return PageImpl(userVoList, page.pageable, page.totalElements)
    }
}