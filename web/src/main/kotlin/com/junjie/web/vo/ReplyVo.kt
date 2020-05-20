package com.junjie.web.vo

import com.junjie.data.database.primary.entity.Reply
import org.springframework.beans.BeanUtils
import java.util.*

class ReplyVo(comment: Reply, var critic: UserInfoVo, var answerer: UserInfoVo) {
    var id: Int = 0
    //评论id
    var commentId: Int = 0
    //图片作者id
    var authorId: Int = 0
    //评论人id
    var criticId: Int = 0
    //回答者id
    var createdBy: Int = 0
    //图片id
    var pictureId: Int = 0

    lateinit var content: String

    lateinit var createDate: Date

    init {
        BeanUtils.copyProperties(comment, this)
    }
}