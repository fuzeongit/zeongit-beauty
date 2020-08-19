package com.zeongit.qiniu.service

import com.zeongit.qiniu.core.component.QiniuConfig
import com.zeongit.qiniu.core.util.QiniuAuth
import com.zeongit.qiniu.model.QiniuImageInfo
import com.qiniu.util.UrlSafeBase64
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate


/**
 * @author fjj
 * 七牛云的服务
 */
@Service
class BucketService(private val qiniuConfig: QiniuConfig) {
    /**
     * 移动目标
     * @param url 路径
     * @param bucket 目标空间
     * @param sourceBucket 源空间
     */
    fun move(url: String, bucket: String, sourceBucket: String): Boolean {
        val sourceNameEncodeBase64 = UrlSafeBase64.encodeToString("$sourceBucket:$url")!!
        val nameEncodeBase64 = UrlSafeBase64.encodeToString("$bucket:$url")!!

        val qiniuUrl = "http://" + qiniuConfig.qiniuHost + "/move/" + sourceNameEncodeBase64 + "/" + nameEncodeBase64

        val auth = QiniuAuth.create(qiniuConfig.qiniuAccessKey, qiniuConfig.qiniuSecretKey)
        val authorizationMap = auth.authorization(qiniuUrl, null, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        val authorization = authorizationMap.get("Authorization") as String

        val client = RestTemplate()
        val headers = HttpHeaders()
        val params = LinkedMultiValueMap<String, String>()
        //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("Host", qiniuConfig.qiniuHost)
        headers.add("Accept", "*/*")
        headers.add("Authorization", authorization)
        val requestEntity = HttpEntity<MultiValueMap<String, String>>(params, headers)
        try {
            client.exchange(qiniuUrl, HttpMethod.POST, requestEntity, String::class.java)
            return true
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 获取图片信息
     */
    fun getImageInfo(url: String, bucketUrl: String): QiniuImageInfo? {
        val client = RestTemplate()
        return client.getForObject("$bucketUrl/$url?imageInfo", QiniuImageInfo::class.java)
    }
}