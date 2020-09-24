package com.zeongit.admin.dto
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class Work {
    var illustId: String? = null
    var illustTitle: String? = null
    var id: String? = null
    var title: String? = null
    var illustType = 0
    @JsonProperty("xRestrict")
    var xRestrict = 0
    var restrict = 0
    var sl = 0
    var url: String? = null
    var description: String? = null
    var tags: List<String>? = null
    var userId: String? = null
    var userName: String? = null
    var width = 0
    var height = 0
    var pageCount = 0
    var isBookmarkable = false
    var isAdContainer = false
    var createDate: Date? = null
    var updateDate: Date? = null
    var profileImageUrl: String? = null
}

class CollectDto {
    var works: List<Work>? = null
    var total = 0
}