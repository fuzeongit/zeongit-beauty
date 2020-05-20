package com.junjie.web.vo

import com.junjie.data.database.primary.entity.Comment
import org.springframework.beans.BeanUtils
import java.util.*

class CommentVo {
    var id: Int = 0
    //图片作者id
    var authorId: Int = 0
    //评论人id
    var createdBy: Int = 0

    var pictureId: Int = 0

    lateinit var content: String

    lateinit var createDate: Date

    lateinit var author: UserInfoVo

    lateinit var critic: UserInfoVo

    constructor()

    constructor(comment: Comment) {
        BeanUtils.copyProperties(comment, this)
    }

    constructor(comment: Comment, author: UserInfoVo, critic: UserInfoVo) {
        BeanUtils.copyProperties(comment, this)
        this.author = author
        this.critic = critic
    }
}