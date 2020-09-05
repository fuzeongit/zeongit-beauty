package com.zeongit.web.controller

import com.zeongit.share.annotations.CurrentUserId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.data.database.primary.entity.Complaint
import com.zeongit.share.annotations.Auth
import com.zeongit.share.exception.ProgramException
import com.zeongit.web.service.ComplaintService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 反馈信息
 * @author fjj
 */
@RestController
@RequestMapping("complaint")
class ComplaintController(private val complaintService: ComplaintService) {

    class SaveDto {
        var pictureId: Int = 0

        lateinit var content: String
    }

    /**
     * 提交举报信息
     */
    @Auth
    @PostMapping("save")
    @RestfulPack
    fun save(@CurrentUserId userId: Int?, @RequestBody dto: SaveDto): Complaint {
        complaintService.exists(userId, dto.pictureId) && throw ProgramException("您已举报该图片")
        return complaintService.save(Complaint(dto.pictureId, dto.content))
    }
}