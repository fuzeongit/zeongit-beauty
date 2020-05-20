package com.junjie.data.database.account.entity

import com.junjie.data.constant.UserState
import com.junjie.data.database.base.BaseEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.*

/**
 * 用户
 *
 * @author fjj
 */
@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("phone"))])
@EntityListeners(AuditingEntityListener::class)
class User() : BaseEntity(), Serializable {
    //手机
    @Column(name = "phone", length = 20)
    lateinit var phone: String

    //密码
    @Column(name = "password", length = 16)
    lateinit var password: String

    //用户状态
    @Column(name = "state")
    var state: UserState = UserState.PASS

    constructor(phone: String, password: String) : this() {
        this.phone = phone
        this.password = password
    }
}
