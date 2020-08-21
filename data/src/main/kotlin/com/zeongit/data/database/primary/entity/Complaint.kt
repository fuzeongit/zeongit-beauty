package com.zeongit.data.database.primary.entity

import com.zeongit.data.constant.ReadState
import com.zeongit.share.entity.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.*


/**
 * 举报图片消息
 * @author fjj
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "complaint", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("created_by", "picture_id"))])
class Complaint() : AskEntity(), Serializable {
    //内容
    @Column(name = "picture_id", columnDefinition = "text")
    var pictureId: Int = 0

    //内容
    @Column(name = "content", columnDefinition = "text")
    lateinit var content: String

    @Column(name = "state")
    var state: ReadState = ReadState.WAIT

    constructor(pictureId: Int, content: String) : this() {
        this.pictureId = pictureId
        this.content = content
    }
}