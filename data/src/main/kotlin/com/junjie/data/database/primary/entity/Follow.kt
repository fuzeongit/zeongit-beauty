package com.junjie.data.database.primary.entity

import com.junjie.core.entity.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.*

/**
 * 关注或粉丝
 * @author fjj
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "follow", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("created_by", "following_id"))])
class Follow() : AskEntity(), Serializable {
    //关注人的id
    @Column(name = "following_id", length = 10)
    var followingId: Int = 0

    constructor(followingId: Int) : this() {
        this.followingId = followingId
    }
}