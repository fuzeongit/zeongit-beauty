package com.junjie.web.vo

import com.junjie.data.constant.CollectState
import com.junjie.data.constant.PrivacyState
import com.junjie.data.constant.SizeType
import com.junjie.data.index.primary.document.PictureDocument
import org.springframework.beans.BeanUtils
import java.util.*

class PictureVo {
    var id: Int = 0

    lateinit var introduction: String

    lateinit var url: String

    lateinit var name: String

    var privacy: PrivacyState = PrivacyState.PUBLIC

    var focus: CollectState = CollectState.STRANGE

    var viewAmount: Long = 0

    var likeAmount: Long = 0

    var width: Long = 0

    var height: Long = 0

    lateinit var sizeType: SizeType
    /**
     * 由于es的查询的原因，在空的情况下也有一条空的数据而不是空数组
     */
    var tagList: List<String> = mutableListOf()
        get() {
            return field.filter { it.isNotEmpty() }
        }
    lateinit var user: UserInfoVo

    lateinit var createDate: Date

    constructor()

    constructor(picture: PictureDocument) {
        BeanUtils.copyProperties(picture, this)
    }
}