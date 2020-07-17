package com.zeongit.web.controller

import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.model.Result
import com.zeongit.qiniu.core.component.QiniuConfig
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.zeongit.qiniu.core.util.Auth as QiniuAuth


@RestController
@RequestMapping("qiniu")
class QiniuController(private val qiniuConfig: QiniuConfig) {

    @Auth(true)
    @GetMapping("getUploadToken")
    @RestfulPack
    fun get(): Result<String> {
        val auth = QiniuAuth.create(qiniuConfig.qiniuAccessKey, qiniuConfig.qiniuSecretKey)
        return Result(200, "", auth.uploadToken(qiniuConfig.qiniuTempBucket))
    }

}


