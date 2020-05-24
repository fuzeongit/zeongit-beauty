package com.junjie.data.database.primary.entity

import com.junjie.core.entity.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.*

/**
 * 评论消息
 * @author fjj
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "comment_message", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("comment_id"))])
class CommentMessage() : AskEntity(), Serializable {
    //评论id
    @Column(name = "comment_id", length = 10)
    var commentId: Int = 0
    //图片作者id
    @Column(name = "author_id", length = 10)
    var authorId: Int = 0
    //图片id
    @Column(name = "picture_id", length = 10)
    var pictureId: Int = 0
    //内容
    @Column(name = "content")
    lateinit var content: String
    // 由于read是数据库保留字
    @Column(name = "review")
    var review: Boolean = false

    constructor(commentId: Int, authorId: Int, pictureId: Int, content: String) : this() {
        this.commentId = commentId
        this.authorId = authorId
        this.pictureId = pictureId
        this.content = content
    }
}