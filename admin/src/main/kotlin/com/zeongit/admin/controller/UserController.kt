package com.zeongit.admin.controller

import com.zeongit.admin.service.PictureService
import com.zeongit.admin.service.PixivPictureService
import com.zeongit.admin.service.UserInfoService
import com.zeongit.admin.service.UserService
import com.zeongit.data.constant.PictureLifeState
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.NotFoundException
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.constant.SizeType
import com.zeongit.data.constant.TransferState
import com.zeongit.data.database.admin.entity.PixivPicture
import com.zeongit.data.database.primary.entity.Picture
import com.zeongit.data.index.primary.document.PictureDocument
import com.zeongit.qiniu.core.component.QiniuConfig
import com.zeongit.qiniu.service.BucketService
import com.zeongit.share.enum.Gender
import com.zeongit.share.database.account.entity.UserInfo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.imageio.ImageIO

@RestController
@RequestMapping("user")
class UserController(
        private val userInfoService: UserInfoService) {

    @GetMapping("listInfo")
    @RestfulPack
    fun paging(name: String?): List<UserInfo> {
        return userInfoService.list(name)
    }
}
