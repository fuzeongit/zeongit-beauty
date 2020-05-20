package com.junjie.web.controller

import com.junjie.core.annotations.CurrentUserInfoId
import com.junjie.core.annotations.RestfulPack
import com.junjie.data.database.primary.entity.Feedback
import com.junjie.web.service.FeedbackService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 反馈信息
 * @author fjj
 */
@RestController
@RequestMapping("feedback")
class FeedbackController(private val feedbackService: FeedbackService) {

    class SaveDto {
        lateinit var content: String

        var email: String? = null
    }

    /**
     * 提交反馈
     */
    @PostMapping("save")
    @RestfulPack
    fun save(@CurrentUserInfoId userId: Int?, @RequestBody dto: SaveDto): Feedback {
        return feedbackService.save(Feedback(dto.content, dto.email))
    }
}