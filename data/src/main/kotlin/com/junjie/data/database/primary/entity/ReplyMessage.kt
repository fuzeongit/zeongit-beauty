package com.junjie.data.database.primary.entity

import com.junjie.data.database.base.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.*

/**
 * 回复消息
 * @author fjj
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "reply_message", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("reply_id"))])
class ReplyMessage() : AskEntity(), Serializable {
    //评论id
    @Column(name = "comment_id", length = 10)
    var commentId: Int = 0
    //回复id
    @Column(name = "reply_id", length = 10)
    var replyId: Int = 0
    //图片作者id
    @Column(name = "author_id", length = 10)
    var authorId: Int = 0
    //图片id
    @Column(name = "picture_id", length = 10)
    var pictureId: Int = 0
    //评论人id
    @Column(name = "critic_id", length = 10)
    var criticId: Int = 0
    //内容
    @Column(name = "content")
    lateinit var content: String
    // 由于read是数据库保留字
    var review: Boolean = false

    constructor(commentId: Int, replyId: Int, authorId: Int, pictureId: Int, criticId: Int, content: String) : this() {
        this.commentId = commentId
        this.replyId = replyId
        this.authorId = authorId
        this.pictureId = pictureId
        this.criticId = criticId
        this.content = content
    }
}