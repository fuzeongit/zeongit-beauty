package com.junjie.data.database.base

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.util.*
import javax.persistence.*

@MappedSuperclass
open class BaseEntity : Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 10)
    var id: Int? = null

    //创建时间 自动生成
    @CreatedDate
    var createDate: Date? = null

    //修改时间 自动生成
    @LastModifiedDate
    var lastModifiedDate: Date? = null
}