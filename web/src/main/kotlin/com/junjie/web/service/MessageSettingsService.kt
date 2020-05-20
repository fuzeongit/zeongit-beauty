package com.junjie.web.service

import com.junjie.data.database.primary.entity.MessageSettings

interface MessageSettingsService {
    fun get(userId: Int): MessageSettings

    fun save(messageSettings: MessageSettings): MessageSettings
}