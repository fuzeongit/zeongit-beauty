package com.junjie.web.core.communal

import com.junjie.core.exception.PermissionException
import com.junjie.data.constant.PrivacyState
import com.junjie.data.index.primary.document.PictureDocument
import com.junjie.web.service.CollectionService
import com.junjie.web.service.PictureDocumentService
import com.junjie.web.vo.PictureVo

abstract class PictureVoAbstract : UserVoAbstract() {
    abstract val pictureDocumentService: PictureDocumentService

    abstract val collectionService: CollectionService

    fun getPictureVo(pictureId: Int, userId: Int? = null): PictureVo {
        val picture = pictureDocumentService.get(pictureId)
        return getPictureVo(picture, userId)
    }

    fun getPictureVo(picture: PictureDocument, userId: Int? = null): PictureVo {
        (picture.privacy == PrivacyState.PRIVATE && picture.createdBy != userId) && throw PermissionException("你没有权限查看该图片")
        val userVo = super.getUserVo(picture.createdBy, userId)
        val pictureVo = PictureVo(picture)
        pictureVo.focus = collectionService.exists(userId, pictureVo.id)
        pictureVo.user = userVo
        return pictureVo
    }
}