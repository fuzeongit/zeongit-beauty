package com.zeongit.web.controller

import com.zeongit.data.constant.BlockState
import com.zeongit.data.database.primary.entity.TagBlackHole
import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.web.service.TagBlackHoleService
import com.zeongit.web.vo.TagBlackHoleVo
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
@RequestMapping("tagBlackHole")
class TagBlackHoleController(
        private val tagBlackHoleService: TagBlackHoleService) {

    class SaveDto {
        lateinit var name: String
    }

    @Auth
    @PostMapping("block")
    @RestfulPack
    fun block(@CurrentUserInfoId userId: Int, @RequestBody dto: SaveDto): BlockState {
        val tag = dto.name
        return if (tagBlackHoleService.exists(userId, tag)) {
            tagBlackHoleService.remove(userId, tag)
            BlockState.NORMAL
        } else {
            tagBlackHoleService.save(tag)
            BlockState.SHIELD
        }
    }

    @Auth
    @GetMapping("paging")
    @RestfulPack
    fun paging(@CurrentUserInfoId userId: Int, @PageableDefault(value = 20) pageable: Pageable, startDate: Date?, endDate: Date?): Page<TagBlackHoleVo> {
        val page = tagBlackHoleService.paging(pageable, userId, startDate, endDate)
        val blackList = page.content.map {
            TagBlackHoleVo(it.tag, BlockState.SHIELD)
        }
        return PageImpl(blackList, page.pageable, page.totalElements)
    }
}