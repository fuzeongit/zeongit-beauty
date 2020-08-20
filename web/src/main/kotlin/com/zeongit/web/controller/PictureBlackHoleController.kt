package com.zeongit.web.controller

import com.zeongit.data.constant.CollectState
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.database.primary.entity.PictureBlackHole
import com.zeongit.data.database.primary.entity.UserBlackHole
import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.exception.PermissionException
import com.zeongit.share.exception.ProgramException
import com.zeongit.web.core.communal.PictureVoAbstract
import com.zeongit.web.service.*
import com.zeongit.web.vo.CollectionPictureVo
import com.zeongit.web.vo.PictureBlackHoleVo
import com.zeongit.web.vo.PictureVo
import com.zeongit.web.vo.UserInfoVo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.util.*


/**
 * @author fjj
 * 图片黑名单的控制器
 */
@RestController
@RequestMapping("pictureBlackHole")
class PictureBlackHoleController(
        override val pictureDocumentService: PictureDocumentService,
        override val collectionService: CollectionService,
        override val userInfoService: UserInfoService,
        override val followService: FollowService,
        private val pictureBlackHoleService: PictureBlackHoleService) : PictureVoAbstract() {

    class SaveDto {
        var targetId: Int = 0
    }

    @Auth
    @PostMapping("save")
    @RestfulPack
    fun save(@CurrentUserInfoId userId: Int, @RequestBody dto: SaveDto): PictureBlackHole {
        val targetId = dto.targetId
        val picture =  pictureDocumentService.get(targetId)
        picture.createdBy == userId && throw ProgramException("不能把自己的作品加入黑名单")
        pictureBlackHoleService.exists(userId, targetId) && throw ProgramException("图片黑名单已存在")
        return pictureBlackHoleService.save(targetId)
    }

    @Auth
    @PostMapping("remove")
    @RestfulPack
    fun remove(@CurrentUserInfoId userId: Int, @RequestBody dto: SaveDto): Boolean {
        val targetId = dto.targetId
        pictureBlackHoleService.exists(userId, targetId) && throw ProgramException("图片黑名单不存在")
        return pictureBlackHoleService.remove(userId, targetId)
    }

    @Auth
    @GetMapping("paging")
    @RestfulPack
    fun paging(@CurrentUserInfoId userId: Int, @PageableDefault(value = 20) pageable: Pageable, startDate: Date?, endDate: Date?): Page<PictureBlackHoleVo> {
        val page = pictureBlackHoleService.paging(pageable, userId, startDate, endDate)
        val blackList = ArrayList<PictureBlackHoleVo>()
        for (pictureBlackHole in page.content) {
            val pictureBlackHoleVo = try {
                val picture = pictureDocumentService.get(pictureBlackHole.targetId)
                //图片被隐藏
                if (picture.privacy == PrivacyState.PRIVATE) {
                    picture.url = ""
                }
                PictureBlackHoleVo(
                        picture,
                        getPictureVo(picture, userId).focus,
                        pictureBlackHole.createDate!!,
                        getUserVo(picture.createdBy, userId))
            } catch (e: NotFoundException) {
                PictureBlackHoleVo(pictureBlackHole.targetId, collectionService.exists(userId, pictureBlackHole.targetId), pictureBlackHole.lastModifiedDate!!)
            } catch (e: PermissionException) {
                PictureBlackHoleVo(pictureBlackHole.targetId, collectionService.exists(userId, pictureBlackHole.targetId), pictureBlackHole.lastModifiedDate!!)
            }
            blackList.add(pictureBlackHoleVo)
        }
        return PageImpl(blackList, page.pageable, page.totalElements)
    }
}