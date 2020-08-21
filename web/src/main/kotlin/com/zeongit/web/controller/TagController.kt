package com.zeongit.web.controller

import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.SignInException
import com.zeongit.web.service.PictureDocumentService
import com.zeongit.web.vo.TagPictureVo
import com.zeongit.web.vo.TagFrequencyVo
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
    fun listTagTop30(@CurrentUserInfoId userId: Int?): List<TagFrequencyVo> {
        return pictureDocumentService.listTagTop30(userId).map {
            TagFrequencyVo(it.keyAsString, it.docCount)
        }
    }

    @GetMapping("listTagAndPictureTop30")
    @RestfulPack
    fun listTagAndPictureTop30(@CurrentUserInfoId userId: Int?): List<TagPictureVo> {
        val tagList = pictureDocumentService.listTagTop30(userId)
        return tagList.map {
            val picture = pictureDocumentService.getFirstByTag(it.keyAsString, userId)
            TagPictureVo(picture.url, it.keyAsString, it.docCount)
        }
    }

    @GetMapping("listTagFrequencyByUserId")
    @RestfulPack
    fun listTagFrequencyByUserId(@CurrentUserInfoId userId: Int?, targetId: Int?): List<TagFrequencyVo> {
        (userId == null && targetId == null) && throw SignInException("请重新登录")
        return pictureDocumentService.listTagByUserId(targetId ?: userId!!).map {
            TagFrequencyVo(it.keyAsString, it.docCount)
        }
    }
}