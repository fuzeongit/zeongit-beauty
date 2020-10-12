package com.zeongit.data.database.admin.entity

import com.zeongit.share.entity.BaseEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "pixiv_work", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("pixivId"))])
@EntityListeners(AuditingEntityListener::class)
class PixivWork() : BaseEntity(), Serializable {
    var illustId: String? = null
    var illustTitle: String? = null
    var pixivId: String? = null
    var title: String? = null
    var illustType = 0
    var xRestrict = 0
    var pixivRestrict = 0
    var sl = 0
    var url: String? = null
    var description: String? = null
    var tags: String? = null
    var userId: String? = null
    var userName: String? = null
    var width = 0
    var height = 0
    var pageCount = 0
    var bookmarkable = false
    var adContainer = false
    var produceDate: Date? = null
    var updateDate: Date? = null
    var profileImageUrl: String? = null
    var originalUrl: String? = null
    var translateTag: String? = null
    var download: Boolean = false
}