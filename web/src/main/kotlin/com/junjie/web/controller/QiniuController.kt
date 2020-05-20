package com.junjie.web.controller

import com.junjie.core.annotations.Auth
import com.junjie.core.annotations.RestfulPack
import com.junjie.core.model.Result
import com.junjie.qiniu.core.component.QiniuConfig
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.junjie.qiniu.core.util.Auth as QiniuAuth


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


