package com.zeongit.web.service

import com.zeongit.data.database.primary.entity.MessageSettings

interface MessageSettingsService {
    fun get(userId: Int): MessageSettings

    fun save(messageSettings: MessageSettings): MessageSettings
}