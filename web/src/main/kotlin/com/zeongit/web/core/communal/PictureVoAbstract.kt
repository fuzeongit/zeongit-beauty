package com.zeongit.web.core.communal

import com.zeongit.share.exception.PermissionException
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.index.primary.document.PictureDocument
import com.zeongit.web.service.CollectionService
import com.zeongit.web.service.PictureDocumentService
import com.zeongit.web.vo.PictureVo

abstract class PictureVoAbstract : UserInfoVoAbstract() {
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