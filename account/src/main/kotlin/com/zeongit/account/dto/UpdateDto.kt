package com.zeongit.account.dto


class UpdateDto : UserDto() {
    lateinit var verificationCode: String
}