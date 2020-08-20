package com.zeongit.web.controller

import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.SignInException
import com.zeongit.web.service.PictureDocumentService
import com.zeongit.web.vo.TagPictureVo
import com.zeongit.web.vo.UserTagFrequencyVo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author fjj
 * 标签的控制器
 */
@RestController
@RequestMapping("tag")
class TagController(private val pictureDocumentService: PictureDocumentService) {
    @GetMapping("listTagTop30")
    @RestfulPack
    fun listTagTop30(@CurrentUserInfoId userId: Int?): List<String> {
        return pictureDocumentService.listTagTop30(userId)
    }

    @GetMapping("listTagAndPictureTop30")
    @RestfulPack
    fun listTagAndPictureTop30(@CurrentUserInfoId userId: Int?): List<TagPictureVo> {
        val tagList = pictureDocumentService.listTagTop30(userId)
        return tagList.map {
            val picture = pictureDocumentService.getFirstByTag(it, userId)
            TagPictureVo(picture.url, it)
        }
    }

    @GetMapping("listTagFrequencyByUserId")
    @RestfulPack
    fun listTagFrequencyByUserId(@CurrentUserInfoId userId: Int?, targetId: Int?): List<UserTagFrequencyVo> {
        (userId == null && targetId == null) && throw SignInException("请重新登录")
        val pictureList = pictureDocumentService.listByUserId(targetId ?: userId!!)
        val tagList = mutableListOf<String>()
        pictureList.forEach { tagList.addAll(it.tagList) }
        val tagSet = tagList.toSet().filter { it.isNotEmpty() }
        return tagSet.map {
            UserTagFrequencyVo(it,
                    tagList.filter { source -> source == it }.size
            )
        }.sortedByDescending { it.amount }
    }
}