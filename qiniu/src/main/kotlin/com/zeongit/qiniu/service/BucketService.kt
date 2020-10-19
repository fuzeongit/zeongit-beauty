package com.zeongit.qiniu.service

import com.qiniu.common.QiniuException
import com.qiniu.storage.BucketManager
import com.qiniu.storage.Configuration
import com.qiniu.storage.Region
import com.qiniu.storage.model.FileListing
import com.qiniu.util.Auth
import com.zeongit.qiniu.core.component.QiniuConfig
import com.zeongit.qiniu.model.QiniuImageInfo
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


/**
 * @author fjj
 * 七牛云的服务
 */
@Service
class BucketService(qiniuConfig: QiniuConfig) {
    //鉴权
    private val auth = Auth.create(qiniuConfig.qiniuAccessKey, qiniuConfig.qiniuSecretKey)

    //空间管理器
    private val bucketManager = BucketManager(auth, Configuration(Region.region0()))

    /**
     * 移动目标
     * @param url 路径
     * @param bucket 目标空间
     * @param sourceBucket 源空间
     */
    fun move(url: String, bucket: String, sourceBucket: String): Boolean {
//        val sourceNameEncodeBase64 = UrlSafeBase64.encodeToString("$sourceBucket:$url")!!
//        val nameEncodeBase64 = UrlSafeBase64.encodeToString("$bucket:$url")!!
//
//        val qiniuUrl = "http://" + qiniuConfig.qiniuHost + "/move/" + sourceNameEncodeBase64 + "/" + nameEncodeBase64
//
//        val auth = QiniuAuth.create(qiniuConfig.qiniuAccessKey, qiniuConfig.qiniuSecretKey)
//        val authorizationMap = auth.authorization(qiniuUrl, null, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//        val authorization = authorizationMap.get("Authorization") as String
//
//        val client = RestTemplate()
//        val headers = HttpHeaders()
//        val params = LinkedMultiValueMap<String, String>()
//        //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
//        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
//        headers.add("Host", qiniuConfig.qiniuHost)
//        headers.add("Accept", "*/*")
//        headers.add("Authorization", authorization)
//        val requestEntity = HttpEntity<MultiValueMap<String, String>>(params, headers)
//        try {
//            client.exchange(qiniuUrl, HttpMethod.POST, requestEntity, String::class.java)
//            return true
//        } catch (e: Exception) {
//            throw e
//        }
        //避免Base64编码中出现/
        return try {
            //第一个参数源目标空间名，第二个是移动前名称，第三个移动到目标空间名，第四个移动后的名称
            bucketManager.move(sourceBucket, url, bucket, url)
            true
        } catch (e: QiniuException) {
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

    /**
     * 获取图片信息
     */
    fun listFile(bucket: String, marker: String): FileListing? {
        return bucketManager.listFiles(bucket, "", marker, 1000, "")
    }
}