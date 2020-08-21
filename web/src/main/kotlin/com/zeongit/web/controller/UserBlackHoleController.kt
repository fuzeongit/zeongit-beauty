package com.zeongit.web.controller

import com.zeongit.data.constant.BlockState
import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.ProgramException
import com.zeongit.web.core.communal.UserInfoVoAbstract
import com.zeongit.web.service.*
import com.zeongit.web.vo.BlackHoleVo
import com.zeongit.web.vo.TagBlackHoleVo
import com.zeongit.web.vo.UserBlackHoleVo
import com.zeongit.web.vo.UserInfoVo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.util.*


/**
 * @author fjj
 * 用户黑名单的控制器
 */
@RestController
@RequestMapping("userBlackHole")
class UserBlackHoleController(
        override val userInfoService: UserInfoService,
        override val followService: FollowService,
        private val pictureDocumentService: PictureDocumentService,
        private val userBlackHoleService: UserBlackHoleService,
        private val tagBlackHoleService: TagBlackHoleService
) : UserInfoVoAbstract() {

    class SaveDto {
        var targetId: Int = 0
    }

    @Auth
    @PostMapping("block")
    @RestfulPack
    fun block(@CurrentUserInfoId userId: Int, @RequestBody dto: SaveDto): BlockState {
        val targetId = dto.targetId
        userId == targetId && throw ProgramException("操作有误")
        return if (userBlackHoleService.exists(userId, targetId)) {
            userBlackHoleService.remove(userId, targetId)
            BlockState.NORMAL
        } else {
            userBlackHoleService.save(targetId)
            BlockState.SHIELD
        }
    }

    /**
     * 获取屏蔽状态
     */
    @Auth
    @GetMapping("get")
    @RestfulPack
    fun get(@CurrentUserInfoId userId: Int, targetId: Int): BlackHoleVo {
        val vo = getUserVo(targetId, userId)
        return BlackHoleVo(
                UserBlackHoleVo(targetId, vo.avatarUrl, vo.nickname,
                        if (userBlackHoleService.exists(targetId, userId)) BlockState.SHIELD else BlockState.NORMAL
                ),
                pictureDocumentService.listTagByUserId(targetId).map { TagBlackHoleVo(it.keyAsString, if (tagBlackHoleService.exists(userId, it.keyAsString)) BlockState.SHIELD else BlockState.NORMAL) }
        )
    }

    @Auth
    @GetMapping("paging")
    @RestfulPack
    fun paging(@CurrentUserInfoId userId: Int, @PageableDefault(value = 20) pageable: Pageable, startDate: Date?, endDate: Date?): Page<UserBlackHoleVo> {
        val page = userBlackHoleService.paging(pageable, userId, startDate, endDate)
        val userVoList = page.content.map {
            val info = getUserVo(it.targetId, userId)
            UserBlackHoleVo(info.id, info.avatarUrl, info.nickname, BlockState.SHIELD)
        }
        return PageImpl(userVoList, page.pageable, page.totalElements)
    }
}