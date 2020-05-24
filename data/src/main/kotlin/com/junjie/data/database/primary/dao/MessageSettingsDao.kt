package com.junjie.data.database.primary.dao

import com.junjie.data.database.primary.entity.MessageSettings
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageSettingsDao : JpaRepository<MessageSettings, Int>