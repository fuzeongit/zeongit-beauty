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

    class IdListDto(var idList: List<Int>)


    @GetMapping("paging")
    @RestfulPack
    fun paging(pageable: Pageable, userId: Int?, name: String?, privacy: PrivacyState?, life: PictureLifeState?, master: Boolean?, startDate: Date?, endDate: Date?, sizeType: SizeType?): Page<Picture> {
        return pictureService.paging(pageable, userId, name, privacy, life, master, startDate, endDate, sizeType)
    }

    /**
     * 更新隐藏状态
     */
    @PostMapping("hide")
    @RestfulPack
    fun hide(@RequestBody idListDto: IdListDto): Boolean {
        for (id in idListDto.idList) {
            val picture = pictureService.get(id)
            picture.privacy = when (picture.privacy) {
                PrivacyState.PRIVATE -> PrivacyState.PUBLIC
                PrivacyState.PUBLIC -> PrivacyState.PRIVATE
            }
            pictureService.save(picture).privacy
        }
        return true
    }

    /**
     * 逻辑删除图片
     */
    @PostMapping("remove")
    @RestfulPack
    fun remove(@RequestBody idListDto: IdListDto): Boolean {
        for (id in idListDto.idList) {
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
    fun reduction(@RequestBody idListDto: IdListDto): Boolean {
        for (id in idListDto.idList) {
            pictureService.reduction(id)
        }
        return true
    }

    /**
     * 物理删除图片
     * 慎用
     */
    @PostMapping("delete")
    @RestfulPack
    fun delete(@RequestBody idListDto: IdListDto): Boolean {
        for (id in idListDto.idList) {
            val picture = pictureService.getByLife(id)
            bucketService.move(picture.url, qiniuConfig.qiniuTemporaryBucket, qiniuConfig.qiniuPictureBucket)
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
    @PostMapping("rename")
    @RestfulPack
    fun rename(): Boolean {
        val list = pictureService.list()
        for (item in list) {
            val pixivPicture = pixivPictureService.getByPictureId(item.id!!)
            val userInfo = userInfoService.get(item.createdBy!!)

            userInfo.introduction = pixivPicture.pixivUserName ?: "镜花水月"
            userInfo.nickname = pixivPicture.pixivUserName ?: "镜花水月"
            userInfoService.save(userInfo)
        }
        return true
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


    /**
     * 获取套图
     */
    @GetMapping("listPictureBySuit")
    @RestfulPack
    fun listPictureBySuit(pixivId: String): List<Picture> {
        val list = pixivPictureService.listByPixivId(pixivId)
        val resultList = mutableListOf<Picture>()
        for (item in list) {
            try {
                resultList.add(pictureService.getByLife(item.pictureId))
            } catch (e: NotFoundException) {
            }
        }
        return resultList
    }
}
