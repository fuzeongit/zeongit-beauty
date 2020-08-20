package com.zeongit.data.database.primary.entity

import com.zeongit.share.entity.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Table


/**
 * 用户黑名单
 * @author fjj
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "picture_black_hole")
class PictureBlackHole() : AskEntity(), Serializable {
    @Column(name = "target_id", length = 10)
    var targetId: Int = 0

    constructor(targetId: Int) : this() {
        this.targetId = targetId
    }
}