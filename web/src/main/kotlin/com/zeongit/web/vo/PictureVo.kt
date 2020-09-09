package com.zeongit.web.vo

import com.zeongit.data.constant.CollectState
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.constant.AspectRatio
import com.zeongit.data.index.primary.document.PictureDocument
import org.springframework.beans.BeanUtils
import java.util.*

class PictureVo() {
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

    lateinit var aspectRatio: AspectRatio

    /**
     * 由于es的查询的原因，在空的情况下也有一条空的数据而不是空数组
     */
    var tagList: List<String> = mutableListOf()
        get() {
            return field.filter { it.isNotEmpty() }
        }
    lateinit var user: UserInfoVo

    lateinit var createDate: Date

    constructor(picture: PictureDocument) : this() {
        BeanUtils.copyProperties(picture, this)
    }
}