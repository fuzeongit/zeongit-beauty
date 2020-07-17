package com.zeongit.data.database.primary.entity

import com.zeongit.share.entity.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Table

/**
 * 回复
 * @author fjj
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "reply")
class Reply() : AskEntity(), Serializable {
    //评论id
    @Column(name = "comment_id", length = 10)
    var commentId: Int = 0
    //图片作者id
    @Column(name = "author_id", length = 10)
    var authorId: Int = 0
    //评论人id
    @Column(name = "critic_id", length = 10)
    var criticId: Int = 0
    //图片id
    @Column(name = "picture_id", length = 10)
    var pictureId: Int = 0
    //内容
    @Column(name = "content")
    lateinit var content: String

    constructor(commentId: Int, authorId: Int, criticId: Int, pictureId: Int, content: String) : this() {
        this.commentId = commentId
        this.authorId = authorId
        this.criticId = criticId
        this.pictureId = pictureId
        this.content = content
    }
}