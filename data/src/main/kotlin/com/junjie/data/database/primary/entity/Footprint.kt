package com.junjie.data.database.primary.entity

import com.junjie.data.database.base.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.*

/**
 * 足迹
 * @author fjj
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "footprint", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("created_by", "picture_id"))])
class Footprint() : AskEntity(), Serializable {
    //图片id
    @Column(name = "picture_id", length = 10)
    var pictureId: Int = 0

    constructor(pictureId: Int) : this() {
        this.pictureId = pictureId
    }
}