package com.junjie.account.dto


class UpdateDto : UserDto() {
    lateinit var verificationCode: String
}