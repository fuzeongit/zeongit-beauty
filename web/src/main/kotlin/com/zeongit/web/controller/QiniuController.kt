package com.zeongit.web.controller

import com.zeongit.qiniu.core.component.QiniuConfig
import com.zeongit.qiniu.core.util.QiniuAuth
import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.constant.ExceptionCodeConstant
import com.zeongit.share.model.Result
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("qiniu")
class QiniuController(private val qiniuConfig: QiniuConfig) {
    @Auth()
    @GetMapping("get")
    @RestfulPack
    fun get(): Result<String> {
        val auth = QiniuAuth.create(qiniuConfig.qiniuAccessKey, qiniuConfig.qiniuSecretKey)
        return Result(ExceptionCodeConstant.SUCCESS, "", auth.uploadToken(qiniuConfig.qiniuTemporaryBucket))
    }
}


