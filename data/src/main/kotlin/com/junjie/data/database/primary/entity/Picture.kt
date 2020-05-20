package com.junjie.data.database.primary.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.junjie.data.constant.PictureLifeState
import com.junjie.data.constant.PictureState
import com.junjie.data.constant.PrivacyState
import com.junjie.data.constant.SizeType
import com.junjie.data.database.base.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.util.*
import javax.persistence.*

/**
 * 画册
 * @author fjj
 */
@Entity
@NamedEntityGraph(name = "Picture.Tag", attributeNodes = [NamedAttributeNode("tagList")])
@Table(name = "picture", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("url"))])
@EntityListeners(AuditingEntityListener::class)
class Picture() : AskEntity(), Serializable {
    //图片地址
    @Column(name = "url")
    lateinit var url: String

    //图片名称
    @Column(name = "name")
    var name: String = "无题"

    //图片简介
    @Column(name = "introduction")
    var introduction: String = "身无彩凤双飞翼，心有灵犀一点通"

    //是否公开
    @Column(name = "privacy")
    var privacy: PrivacyState = PrivacyState.PUBLIC

    //图片状态
    @Column(name = "state")
    var state: PictureState = PictureState.PASS

    //是否存在，不存在不建立索引
    @Column(name = "life")
    var life: PictureLifeState = PictureLifeState.EXIST

    //宽度
    @Column(name = "width")
    var width: Long = 0

    //高度
    @Column(name = "height")
    var height: Long = 0

    //图片类型
    @Column(name = "size_type")
    lateinit var sizeType: SizeType

    //标签列表
    @JsonIgnore
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "picture_id")
    var tagList: MutableSet<Tag> = TreeSet { o1, o2 -> o1.name.compareTo(o2.name) }


    constructor(url: String, width: Long, height: Long, name: String? = "无题", introduction: String? = "身无彩凤双飞翼，心有灵犀一点通", privacy: PrivacyState = PrivacyState.PUBLIC) : this() {
        this.url = url
        this.name = name ?: "无题"
        this.introduction = introduction ?: "身无彩凤双飞翼，心有灵犀一点通"
        this.width = width
        this.height = height
        this.privacy = privacy
        this.sizeType = when {
            width > height -> SizeType.TRANSVERSE
            width < height -> SizeType.VERTICAL
            else -> SizeType.SQUARE
        }
    }
}