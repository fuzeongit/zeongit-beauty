package com.zeongit.web.vo

import com.zeongit.data.constant.CollectState
import com.zeongit.data.constant.PictureLifeState
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.index.primary.document.PictureDocument
import java.util.*

/**
 * 收藏图片的vo
 * @author fjj
 * 这里的id是图片的id，创建时间为收藏的创建时间
 */
class CollectionPictureVo {
    var id: Int = 0

    var url: String? = null

    var life: PictureLifeState = PictureLifeState.EXIST

    var privacy: PrivacyState = PrivacyState.PUBLIC

    var focus: CollectState = CollectState.STRANGE

    var width: Long = 0

    var height: Long = 0

    var user: UserInfoVo? = null

    var lastModifiedDate: Date? = null

    constructor(id: Int, focus: CollectState, lastModifiedDate: Date) {
        this.id = id
        this.url = ""
        this.focus = focus
        this.lastModifiedDate = lastModifiedDate
        this.life = PictureLifeState.DISAPPEAR
    }

    constructor(picture: PictureDocument, focus: CollectState, lastModifiedDate: Date, user: UserInfoVo) {
        this.id = picture.id
        this.url = picture.url
        this.privacy = picture.privacy
        this.width = picture.width
        this.height = picture.height
        this.lastModifiedDate = lastModifiedDate
        this.focus = focus
        this.user = user
    }
}