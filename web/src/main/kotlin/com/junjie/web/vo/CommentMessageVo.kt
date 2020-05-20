package com.junjie.web.vo

import com.junjie.data.database.primary.entity.CommentMessage
import org.springframework.beans.BeanUtils
import java.util.*

class CommentMessageVo {
    lateinit var id: String
    //评论id
    lateinit var commentId: String
    //图片作者id
    lateinit var authorId: String
    //图片id
    lateinit var pictureId: String
    //评论人id
    lateinit var createdBy: String

    lateinit var content: String

    lateinit var critic: UserInfoVo

    lateinit var createDate: Date

    constructor()

    constructor(commentMessage: CommentMessage) {
        BeanUtils.copyProperties(commentMessage, this)
    }

    constructor(commentMessage: CommentMessage, critic: UserInfoVo) {
        BeanUtils.copyProperties(commentMessage, this)
        this.critic = critic
    }
}