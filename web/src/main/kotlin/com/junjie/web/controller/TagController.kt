package com.junjie.web.controller

import com.junjie.core.annotations.RestfulPack
import com.junjie.web.service.PictureDocumentService
import com.junjie.web.vo.TagPictureVo
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
    fun listTagTop30(): List<String> {
        return pictureDocumentService.listTagTop30()
    }

    @GetMapping("listTagAndPictureTop30")
    @RestfulPack
    fun listTagAndPictureTop30(): List<TagPictureVo> {
        val tagList = pictureDocumentService.listTagTop30()
        return tagList.map {
            val picture = pictureDocumentService.getFirstByTag(it)
            TagPictureVo(picture.url, it)
        }
    }
}