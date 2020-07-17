package com.zeongit.web.vo

import com.zeongit.data.database.primary.entity.FollowMessage
import org.springframework.beans.BeanUtils
import java.util.*

class FollowMessageVo {
    lateinit var id: String

    lateinit var createdBy: String

    lateinit var followingId: String

    lateinit var createDate: Date

    lateinit var follower: UserInfoVo

    constructor()

    constructor(followMessage: FollowMessage) {
        BeanUtils.copyProperties(followMessage, this)
    }

    constructor(followMessage: FollowMessage, follower: UserInfoVo) {
        BeanUtils.copyProperties(followMessage, this)
        this.follower = follower
    }
}