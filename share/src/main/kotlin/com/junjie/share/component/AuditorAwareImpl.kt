package com.junjie.share.component

import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*


@Component
class AuditorAwareImpl : AuditorAware<Int> {
    override fun getCurrentAuditor(): Optional<Int> {
        return Optional.ofNullable(try {
            (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request.getAttribute("user_info_id") as Int
        } catch (e: Exception) {
            null
        })
    }
}