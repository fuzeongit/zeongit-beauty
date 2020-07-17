package com.zeongit.web.vo

import com.zeongit.data.database.primary.entity.ReplyMessage
import org.springframework.beans.BeanUtils
import java.util.*

class ReplyMessageVo(replyMessage: ReplyMessage, var answerer: UserInfoVo) {
    lateinit var id: String
    //评论id
    lateinit var commentId: String
    //回复id
    lateinit var replyId: String
    //图片作者id
    lateinit var authorId: String
    //图片id
    lateinit var pictureId: String
    //评论人id
    lateinit var criticId: String
    //回答者id
    lateinit var createdBy: String

    lateinit var content: String

    lateinit var createDate: Date

    init {
        BeanUtils.copyProperties(replyMessage, this)
    }
}