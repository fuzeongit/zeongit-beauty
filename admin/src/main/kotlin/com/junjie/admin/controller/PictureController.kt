package com.junjie.admin.controller

import com.junjie.admin.service.PictureService
import com.junjie.admin.service.PixivPictureService
import com.junjie.core.annotations.RestfulPack
import com.junjie.core.exception.NotFoundException
import com.junjie.data.constant.PrivacyState
import com.junjie.data.constant.TransferState
import com.junjie.data.database.admin.entity.PixivPicture
import com.junjie.data.database.primary.entity.Picture
import com.junjie.data.index.primary.document.PictureDocument
import com.junjie.qiniu.core.component.QiniuConfig
import com.junjie.qiniu.service.BucketService
import com.junjie.share.constant.Gender
import com.junjie.share.database.account.entity.UserInfo
import com.junjie.share.service.UserInfoService
import com.junjie.share.service.UserService
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.imageio.ImageIO

@RestController
@RequestMapping("picture")
class PictureController(
        private val bucketService: BucketService,
        private val qiniuConfig: QiniuConfig,
        private val userService: UserService,
        private val userInfoService: UserInfoService,
        private val pictureService: PictureService,
        private val pixivPictureService: PixivPictureService,
        private val elasticsearchTemplate: ElasticsearchTemplate) {

    class PictureInitVO(var errorUrlList: List<String>, var errorReadList: List<String>, var readNumber: Int)

    /**
     * 更新隐藏状态
     */
    @PostMapping("updatePrivacy")
    @RestfulPack
    fun updatePrivacy(id: Int): Boolean {
        val picture = pictureService.get(id)
        picture.privacy = when (picture.privacy) {
            PrivacyState.PRIVATE -> PrivacyState.PUBLIC
            PrivacyState.PUBLIC -> PrivacyState.PRIVATE
        }
        pictureService.save(picture)
        return true
    }

    /**
     * 逻辑删除图片
     */
    @PostMapping("batchRemove")
    @RestfulPack
    fun batchRemove(@RequestParam("idList") idList: Array<Int>): Boolean {
        for (id in idList) {
            val picture = pictureService.get(id)
            pictureService.remove(picture)
        }
        return true
    }

    /**
     * 还原
     */
    @PostMapping("reduction")
    @RestfulPack
    fun reduction(@RequestParam("idList") idList: Array<Int>): Boolean {
        for (id in idList) {
            pictureService.reduction(id)
        }
        return true
    }

    /**
     * 物理删除图片
     * 慎用
     */
    @PostMapping("batchDelete")
    @RestfulPack
    fun batchDelete(@RequestParam("idList") idList: Array<Int>): Boolean {
        for (id in idList) {
            val picture = pictureService.getByLife(id)
            bucketService.move(picture.url, qiniuConfig.qiniuTempBucket, qiniuConfig.qiniuBucket)
            pictureService.delete(id)
        }
        return true
    }


    /**
     * 根据文件夹写入图片写入数据库
     * 本地使用
     */
    @PostMapping("init")
    @RestfulPack
    fun init(folderPath: String, userId: Int, privacy: PrivacyState): PictureInitVO {
        var readNumber = 0
        val errorUrlList = mutableListOf<String>()
        val errorReadList = mutableListOf<String>()
        val fileNameList = File(folderPath).list() ?: arrayOf()
        fileNameList.toList().filter { it.toLowerCase().endsWith(".png") || it.toLowerCase().endsWith(".jpg") || it.toLowerCase().endsWith(".jpeg") }
        //绑定到临时id

        for (fileName in fileNameList) {
            val read = try {
                val picture = File("$folderPath/$fileName")
                ImageIO.read(FileInputStream(picture))
            } catch (e: Exception) {
                errorReadList.add(fileName)
                continue
            }
            val picture = Picture(
                    fileName,
                    read.width.toLong(),
                    read.height.toLong(),
                    fileName,
                    "这是一张很好看的图片，这是我从p站上下载回来的，侵删！",
                    privacy)

            picture.createdBy = userId
            picture.lastModifiedBy = userId
            try {
                val pictureDocument = pictureService.save(picture)
                val pixivPicture = PixivPicture(fileName.split("_")[0], pictureDocument.id)
                pixivPictureService.save(pixivPicture)
                readNumber++
            } catch (e: Exception) {
                errorUrlList.add(fileName)
            }
        }
        return PictureInitVO(errorUrlList, errorReadList, readNumber)
    }

    /**
     * 绑定user
     */
    @PostMapping("bindUser")
    @RestfulPack
    fun bindUser(userId: Int): Boolean {
        //临时id
        val pictureList = pictureService.listByUserId(userId)
        for (picture in pictureList) {
            val pixivPicture = pixivPictureService.getByPictureId(picture.id!!)
            if (pixivPicture.state == TransferState.SUCCESS) {
                val info = try {
                    val pixivUser = pixivPictureService.getAccountByPixivUserId(pixivPicture.pixivUserId!!)
                    userInfoService.get(pixivUser.userId)
                } catch (e: NotFoundException) {
                    val info = initUser()
                    pixivPictureService.saveAccount(info.id!!, pixivPicture.pixivUserId!!)
                    info
                }
                picture.createdBy = info.id!!
                picture.lastModifiedBy = info.id!!
                pictureService.save(picture)
            }
        }
        return true
    }

    /**
     * 绑定user
     */
    @PostMapping("bindUser2")
    @RestfulPack
    fun bindUser2(): Boolean {
        val pictureList = pictureService.list()
        for (picture in pictureList) {
            try {
                val pixivPicture = pixivPictureService.getByPictureId(picture.id!!)
                if (pixivPicture.pixivUserId != null) {
                    val info = try {
                        val pixivUser = pixivPictureService.getAccountByPixivUserId(pixivPicture.pixivUserId!!)
                        userInfoService.get(pixivUser.userId)
                    } catch (e: NotFoundException) {
                        val info = initUser()
                        pixivPictureService.saveAccount(info.id!!, pixivPicture.pixivUserId!!)
                        info
                    }
                    picture.createdBy = info.id!!
                    picture.lastModifiedBy = info.id!!
                    pictureService.save(picture)
                }
            } catch (e: Exception) {
                val pixivPicture = PixivPicture(picture.url.split("_")[0], picture.id!!)
                pixivPictureService.save(pixivPicture)
            }
        }
        return true
    }

    /**
     * 建立ES索引
     */
    @PostMapping("initIndex")
    @RestfulPack
    fun initIndex(): Boolean {
        elasticsearchTemplate.createIndex(PictureDocument::class.java)
        return true
    }

    /**
     * 初始化进ES
     */
    @PostMapping("importES")
    @RestfulPack
    fun importES(): Long {
        return pictureService.synchronizationIndexPicture()
    }

    /**
     * 初始化进ES
     */
    @PostMapping("push")
    @RestfulPack
    fun push(userId: Int): Int {
        val list = pictureService.listByUserId(userId)
        var amount = 0
        list.forEach { item ->
            val pixivPicture = PixivPicture(item.url.split("_")[0], item.id!!)
            pixivPictureService.save(pixivPicture)
            amount++
        }
        return amount
    }


    fun initUser(): UserInfo {
        var phone = Random().nextInt(10)
        while (userService.existsByPhone(phone.toString())) {
            phone += Random().nextInt(1000)
        }
        val user = userService.signUp(phone.toString(), "123456")
        val gender = if (phone % 2 == 0) Gender.FEMALE else Gender.MALE
        val info = UserInfo(gender = gender)
        info.userId = user.id!!
        return userInfoService.save(info)
    }
}
