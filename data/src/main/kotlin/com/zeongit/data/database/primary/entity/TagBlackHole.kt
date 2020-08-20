package com.zeongit.data.database.primary.entity

import com.zeongit.share.entity.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Table


/**
 * 标签黑名单
 * @author fjj
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "tag_black_hole")
class TagBlackHole() : AskEntity(), Serializable {
    @Column(name = "tag", length = 32)
    lateinit var tag: String

    constructor(tag: String) : this() {
        this.tag = tag
    }
}