package com.zeongit.web.controller

import com.zeongit.data.database.primary.entity.TagBlackHole
import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.ProgramException
import com.zeongit.web.service.TagBlackHoleService
import org.springframework.data.domain.Page
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
        lateinit var tag: String
    }

    @Auth
    @PostMapping("save")
    @RestfulPack
    fun save(@CurrentUserInfoId userId: Int, @RequestBody dto: SaveDto): TagBlackHole {
        val tag = dto.tag
        tagBlackHoleService.exists(userId, tag) && throw ProgramException("标签黑名单已存在")
        return tagBlackHoleService.save(tag)
    }

    @Auth
    @PostMapping("remove")
    @RestfulPack
    fun remove(@CurrentUserInfoId userId: Int, @RequestBody dto: SaveDto): Boolean {
        val tag = dto.tag
        tagBlackHoleService.exists(userId, tag) && throw ProgramException("标签黑名单不存在")
        return tagBlackHoleService.remove(userId, tag)
    }

    @Auth
    @GetMapping("paging")
    @RestfulPack
    fun paging(@CurrentUserInfoId userId: Int, @PageableDefault(value = 20) pageable: Pageable, startDate: Date?, endDate: Date?): Page<TagBlackHole> {
        return tagBlackHoleService.paging(pageable, userId, startDate, endDate)
    }
}