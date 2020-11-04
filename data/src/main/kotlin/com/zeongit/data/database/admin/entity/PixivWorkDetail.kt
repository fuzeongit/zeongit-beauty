package com.zeongit.data.database.admin.entity

import com.zeongit.share.entity.BaseEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "pixiv_work_detail", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("name"))])
@EntityListeners(AuditingEntityListener::class)
class PixivWorkDetail() : BaseEntity(), Serializable {
    var pixivId: String? = null

    @Column(name = "name")
    lateinit var name: String
    lateinit var url: String
    lateinit var proxyUrl: String
    var xRestrict = 0
    var pixivRestrict = 0
    var download: Boolean = false
    var width = 0
    var height = 0

    @Column(name = "pixiv_using")
    var using: Boolean = false

    constructor(
            pixivId: String,
            name: String,
            url: String,
            proxyUrl: String,
            xRestrict: Int,
            pixivRestrict: Int
    ) : this() {
        this.pixivId = pixivId
        this.name = name
        this.url = url
        this.proxyUrl = proxyUrl
        this.xRestrict = xRestrict
        this.pixivRestrict = pixivRestrict
    }
}