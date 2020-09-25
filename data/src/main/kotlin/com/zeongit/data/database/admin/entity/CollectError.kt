package com.zeongit.data.database.admin.entity

import com.zeongit.share.entity.BaseEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Table

/**
 * 采集异常
 * @author fjj
 */
@Entity
@Table(name = "collect_error")
@EntityListeners(AuditingEntityListener::class)
class CollectError() : BaseEntity(), Serializable {
    lateinit var pixivId: String

    var message: String? = null

    constructor(pixivId: String, message: String?) : this() {
        this.pixivId = pixivId
        this.message = message
    }
}