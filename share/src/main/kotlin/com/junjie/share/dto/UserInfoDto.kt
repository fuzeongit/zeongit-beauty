package com.junjie.share.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.junjie.share.constant.Gender
import java.util.*

class UserInfoDto {
    var nickname: String? = null

    var gender: Gender? = null
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    var birthday: Date? = null

    var introduction: String? = null

    var country: String? = null

    var province: String? = null

    var city: String? = null

    var avatarUrl: String? = null

    var background: String? = null
}