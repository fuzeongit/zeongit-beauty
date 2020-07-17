package com.zeongit.data.database.admin.entity

import com.zeongit.share.entity.BaseEntity
import com.zeongit.data.constant.TransferState
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Table

/**
 * 画册
 * @author fjj
 */
@Entity
@Table(name = "pixiv_picture")
@EntityListeners(AuditingEntityListener::class)
class PixivPicture() : BaseEntity(), Serializable {
    lateinit var pixivId: String

    var pictureId: Int = 0

    var name: String? = null

    var pixivUserName: String? = null

    var pixivUserId: String? = null
    //竖线隔开
    var tagList: String? = null

    var state: TransferState = TransferState.WAIT

    constructor(pixivId: String, pictureId: Int) : this() {
        this.pixivId = pixivId
        this.pictureId = pictureId
    }
}