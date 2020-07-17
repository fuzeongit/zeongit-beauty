package com.zeongit.web.model

import java.security.Principal


class WebSocketUser(private val id: String) : Principal {
    override fun getName(): String {
        return id
    }
}