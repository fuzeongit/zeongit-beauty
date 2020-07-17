package com.zeongit.data.database.admin.entity


import com.zeongit.share.entity.BaseEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.EntityListeners

/**
 * 账号和pixiv账号的中间表
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
class PixivUser() : BaseEntity(), Serializable {
    var userId: Int = 0

    lateinit var pixivUserId: String

    constructor(userId: Int, pixivUserId: String) : this() {
        this.userId = userId
        this.pixivUserId = pixivUserId
    }
}