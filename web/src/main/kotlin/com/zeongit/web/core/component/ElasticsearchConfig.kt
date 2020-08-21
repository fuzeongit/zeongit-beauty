package com.zeongit.web.core.component

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("elasticsearch")
class ElasticsearchConfig {
    lateinit var pictureSearch: String
}