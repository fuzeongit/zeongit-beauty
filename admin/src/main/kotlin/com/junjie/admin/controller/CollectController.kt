package com.junjie.admin.controller

import com.junjie.admin.service.PictureService
import com.junjie.admin.service.PixivPictureService
import com.junjie.core.annotations.RestfulPack
import com.junjie.core.exception.NotFoundException
import com.junjie.core.util.EmojiUtil
import com.junjie.data.constant.TransferState
import com.junjie.data.database.admin.entity.PixivPicture
import com.junjie.data.database.primary.entity.Picture
import com.junjie.data.database.primary.entity.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 供插件使用的
 * @author fjj
 */
@RestController
@RequestMapping("collect")
class CollectController(
        private val pictureService: PictureService,
        private val pixivPictureService: PixivPictureService) {
    /**
     * 获取采集标签任务
     */
    @GetMapping("listTagTask")
    @RestfulPack
    fun listTagTask(state: TransferState?): List<PixivPicture> {
        return pixivPictureService.listByState(state ?: TransferState.WAIT)
    }

    /**
     * 保存采集
     */
    @PostMapping("save")
    @RestfulPack
    fun pixivPictureSave(pixivId: String, name: String, userName: String, userId: String, tagString: String): Boolean {
        val pixivPictureList = pixivPictureService.listByPixivId(pixivId)
        for (pixivPicture in pixivPictureList) {
            if (pixivPicture.state != TransferState.WAIT) continue
            pixivPicture.pixivId = pixivId
            pixivPicture.name = EmojiUtil.emojiChange(name).trim()
            pixivPicture.pixivUserName = EmojiUtil.emojiChange(userName).trim()
            pixivPicture.pixivUserId = userId
            pixivPicture.tagList = EmojiUtil.emojiChange(tagString).trim()
            pixivPicture.state = TransferState.SUCCESS
            try {
                val picture = pictureService.get(pixivPicture.pictureId)
                picture.name = pixivPicture.name!!
                picture.tagList.addAll(pixivPicture.tagList!!.split("|").asSequence().toSet().asSequence().map { Tag(it) }.toList())
                pixivPictureService.save(pixivPicture)
                pictureService.save(picture, true)
            } catch (e: Exception) {
            }
        }
        return true
    }

    /**
     * 获取套图
     */
    @GetMapping("listPictureBySuit")
    @RestfulPack
    fun listPictureBySuit(pixivId: String): List<Picture> {
        val list = pixivPictureService.listByPixivId(pixivId)
        val resultList = mutableListOf<Picture>()
        for (item in list) {
            try {
                resultList.add(pictureService.getByLife(item.pictureId))
            } catch (e: NotFoundException) {
            }
        }
        return resultList
    }
}