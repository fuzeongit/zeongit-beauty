package com.zeongit.web.controller

import com.zeongit.data.constant.BlockState
import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.ProgramException
import com.zeongit.web.core.communal.UserInfoVoAbstract
import com.zeongit.web.service.FollowService
import com.zeongit.web.service.UserBlackHoleService
import com.zeongit.web.service.UserInfoService
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
        private val userBlackHoleService: UserBlackHoleService) : UserInfoVoAbstract() {

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

    @Auth
    @GetMapping("paging")
    @RestfulPack
    fun paging(@CurrentUserInfoId userId: Int, @PageableDefault(value = 20) pageable: Pageable, startDate: Date?, endDate: Date?): Page<UserInfoVo> {
        val page = userBlackHoleService.paging(pageable, userId, startDate, endDate)
        val userVoList = page.content.map {
            getUserVo(it.createdBy!!, userId)
        }
        return PageImpl(userVoList, page.pageable, page.totalElements)
    }
}