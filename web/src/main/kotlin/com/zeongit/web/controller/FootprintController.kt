package com.zeongit.web.controller

import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.exception.PermissionException
import com.zeongit.share.exception.SignInException
import com.zeongit.data.constant.PrivacyState
import com.zeongit.web.core.communal.PictureVoAbstract
import com.zeongit.web.service.*
import com.zeongit.web.vo.FootprintPictureVo
import com.zeongit.web.vo.UserInfoVo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*


/**
 * @author fjj
 * 足迹的控制器
 */
@RestController
@RequestMapping("footprint")
class FootprintController(private val footprintService: FootprintService,
                          override val pictureDocumentService: PictureDocumentService,
                          override val collectionService: CollectionService,
                          override val userInfoService: UserInfoService,
                          override val followService: FollowService) : PictureVoAbstract() {

    class SaveDto {
        var pictureId: Int = 0
    }

    @Auth
    @PostMapping("save")
    @RestfulPack
    fun save(@CurrentUserId userId: Int, @RequestBody dto: SaveDto): Long {
        val pictureId = dto.pictureId
        val picture = pictureDocumentService.get(pictureId)
        picture.privacy == PrivacyState.PRIVATE && throw PermissionException("私有图片不能操作")
        try {
            footprintService.update(userId, pictureId)
        } catch (e: NotFoundException) {
            footprintService.save(pictureId)
            // 由于足迹有时效性，所以不能通过表来统计
            pictureDocumentService.saveViewAmount(picture, picture.viewAmount + 1)
        }
        return picture.viewAmount
    }


    @GetMapping("paging")
    @RestfulPack
    fun paging(@CurrentUserId userId: Int?, targetId: Int?, @PageableDefault(value = 20) pageable: Pageable): Page<FootprintPictureVo> {
        (userId == null && targetId == null) && throw SignInException("请重新登录")
        val page = footprintService.paging(pageable, targetId ?: userId!!)
        val footprintPictureVoList = ArrayList<FootprintPictureVo>()
        for (footprint in page.content) {
            val footprintPictureVo = try {
                val picture = pictureDocumentService.get(footprint.pictureId)
                //图片被隐藏
                if (picture.privacy == PrivacyState.PRIVATE) {
                    picture.url = ""
                }
                FootprintPictureVo(
                        picture,
                        getPictureVo(picture, userId).focus,
                        footprint.lastModifiedDate!!,
                        getUserVo(picture.createdBy, userId)
                )
            } catch (e: NotFoundException) {
                FootprintPictureVo(footprint.pictureId, collectionService.exists(targetId, footprint.pictureId), footprint.lastModifiedDate!!)
            } catch (e: PermissionException) {
                FootprintPictureVo(footprint.pictureId, collectionService.exists(targetId, footprint.pictureId), footprint.lastModifiedDate!!)
            }
            footprintPictureVoList.add(footprintPictureVo)
        }
        return PageImpl(footprintPictureVoList, page.pageable, page.totalElements)
    }

    @GetMapping("pagingUser")
    @RestfulPack
    fun pagingUser(@CurrentUserId userId: Int?, pictureId: Int, @PageableDefault(value = 20) pageable: Pageable): Page<UserInfoVo> {
        val page = footprintService.pagingByPictureId(pageable, pictureId)
        val userVoList = page.content.map {
            getUserVo(it.createdBy!!, userId)
        }
        return PageImpl(userVoList, page.pageable, page.totalElements)
    }
}