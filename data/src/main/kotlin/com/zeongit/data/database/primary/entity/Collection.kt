package com.zeongit.data.database.primary.entity

import com.zeongit.share.entity.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.*

/**
 * 收藏
 * @author fjj
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "collection", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("created_by", "picture_id"))])
class Collection() : AskEntity(), Serializable {
    //图片id
    @Column(name = "picture_id", length = 10)
    var pictureId: Int = 0

    constructor(pictureId: Int) : this() {
        this.pictureId = pictureId
    }
}