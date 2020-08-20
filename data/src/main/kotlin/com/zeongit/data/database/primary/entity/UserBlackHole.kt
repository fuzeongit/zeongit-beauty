package com.zeongit.data.database.primary.entity

import com.zeongit.share.entity.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.*


/**
 * 用户黑名单
 * @author fjj
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "user_black_hole", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("created_by", "target_id"))])
class UserBlackHole() : AskEntity(), Serializable {
    @Column(name = "target_id", length = 10)
    var targetId: Int = 0

    constructor(targetId: Int) : this() {
        this.targetId = targetId
    }
}