package com.zeongit.web.controller

import com.zeongit.data.constant.BlockState
import com.zeongit.data.constant.PrivacyState
import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.exception.PermissionException
import com.zeongit.share.exception.ProgramException
import com.zeongit.web.core.communal.PictureVoAbstract
import com.zeongit.web.service.*
import com.zeongit.web.vo.PictureBlackHoleVo
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
    @PostMapping("block")
    @RestfulPack
    fun block(@CurrentUserInfoId userId: Int, @RequestBody dto: SaveDto): BlockState {
        val targetId = dto.targetId
        val picture = pictureDocumentService.get(targetId)
        picture.createdBy == userId && throw ProgramException("不能把自己的作品加入黑名单")
        return if (pictureBlackHoleService.exists(userId, targetId)) {
            pictureBlackHoleService.remove(userId, targetId)
            BlockState.NORMAL
        } else {
            pictureBlackHoleService.save(targetId)
            BlockState.SHIELD
        }
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