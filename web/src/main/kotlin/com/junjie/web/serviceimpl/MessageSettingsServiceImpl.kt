package com.junjie.web.serviceimpl

import com.junjie.core.exception.NotFoundException
import com.junjie.data.database.primary.dao.MessageSettingsDao
import com.junjie.data.database.primary.entity.MessageSettings
import com.junjie.web.service.MessageSettingsService
import org.springframework.cache.annotation.CachePut
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.stereotype.Service

@Service
class MessageSettingsServiceImpl(private val messageSettingsDao: MessageSettingsDao) : MessageSettingsService {
    override fun get(userId: Int): MessageSettings {
        val query = MessageSettings()
        query.createdBy = userId
        val matcher = ExampleMatcher.matching()
                .withMatcher("createdBy", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnorePaths("id")
                .withIgnorePaths("commentStatus")
                .withIgnorePaths("replyStatus")
                .withIgnorePaths("followStatus")
                .withIgnorePaths("systemStatus")
                .withIgnorePaths("createdBy")
                .withIgnorePaths("createDate")
                .withIgnorePaths("modifiedDate")
        val example = Example.of(query, matcher)
        return try {
            messageSettingsDao.findOne(example).orElseThrow { NotFoundException("找不到消息配置") }
        } catch (e: NotFoundException) {
            val newSettings = MessageSettings()
            messageSettingsDao.save(newSettings)
        }
    }

    @CachePut("message::settings::get", key = "#messageSettings.createdBy")
    override fun save(messageSettings: MessageSettings): MessageSettings {
        return messageSettingsDao.save(messageSettings)
    }
}