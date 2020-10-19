package com.zeongit.admin.controller

import com.qiniu.storage.model.FileInfo
import com.zeongit.admin.dto.CollectDto
import com.zeongit.admin.dto.UpdateOriginalUrlDto
import com.zeongit.admin.dto.Work
import com.zeongit.admin.service.*
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.constant.TransferState
import com.zeongit.data.database.admin.entity.CollectError
import com.zeongit.data.database.admin.entity.PixivPicture
import com.zeongit.data.database.admin.entity.PixivWork
import com.zeongit.data.database.admin.entity.PixivWorkDetail
import com.zeongit.data.database.primary.entity.Picture
import com.zeongit.data.database.primary.entity.Tag
import com.zeongit.qiniu.core.component.QiniuConfig
import com.zeongit.qiniu.service.BucketService
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.database.account.entity.UserInfo
import com.zeongit.share.enum.Gender
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.model.Result
import com.zeongit.share.util.EmojiUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.io.*
import java.util.*
import javax.imageio.ImageIO


/**
 * 供插件使用的
 * @author fjj
 */
@RestController
@RequestMapping("collect")
class CollectController(
        private val pictureService: PictureService,
        private val userService: UserService,
        private val userInfoService: UserInfoService,
        private val pixivPictureService: PixivPictureService,
        private val pixivWorkService: PixivWorkService,
        private val pixivWorkDetailService: PixivWorkDetailService,
        private val collectErrorService: CollectErrorService
) {
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
                    val info = initUser(pixivPicture.pixivUserName)
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



    @GetMapping("test")
    @RestfulPack
    fun test(): Boolean {
        val list = pixivWorkService.list()
        for (work in list) {
            val originalUrl = work.originalUrl
            if (originalUrl != null && originalUrl.startsWith("https://i.pximg.net/")) {
                for (i in 0 until work.pageCount) {
                    val pictureName = originalUrl.split("/").last()
                    val suitPictureName = pictureName.replace("p0", "p$i")
                    val suitUrl = originalUrl.replace(pictureName, suitPictureName)
                    pixivWorkDetailService.save(PixivWorkDetail(
                            work.pixivId!!,
                            pictureName,
                            suitUrl,
                            suitUrl.replace("https://i.pximg.net/",
                                    "https://pixiv.zeongit.workers.dev/"),
                            work.xRestrict,
                            work.pixivRestrict
                    ))
                }
            }
        }
        return true
    }

    @PostMapping("insert")
    @RestfulPack
    fun insert(@RequestBody collectDto: CollectDto): Boolean {
        for (work in collectDto.works ?: listOf()) {
            try {
                val pixivWork = try {
                    pixivWorkService.getByPixivId(work.id!!)
                } catch (e: Exception) {
                    PixivWork()
                }
                pixivWork.illustId = work.illustId
                pixivWork.illustTitle = EmojiUtil.emojiChange(work.illustTitle ?: "").trim()
                pixivWork.pixivId = work.id
                pixivWork.title = EmojiUtil.emojiChange(work.title ?: "").trim()
                pixivWork.illustType = work.illustType
                pixivWork.xRestrict = work.xRestrict
                pixivWork.pixivRestrict = work.restrict
                pixivWork.sl = work.sl
                pixivWork.description = EmojiUtil.emojiChange(work.description ?: "").trim()
                pixivWork.tags = work.tags?.joinToString("|")
                pixivWork.userId = work.userId
                pixivWork.userName = EmojiUtil.emojiChange(work.userName ?: "").trim()
                pixivWork.width = work.width
                pixivWork.height = work.height
                pixivWork.pageCount = work.pageCount
                pixivWork.bookmarkable = work.isBookmarkable
                pixivWork.adContainer = work.isAdContainer
                pixivWork.produceDate = work.createDate
                pixivWork.updateDate = work.updateDate

                pixivWorkService.save(pixivWork)
            } catch (e: Exception) {
                collectErrorService.save(CollectError(work.illustId ?: "", "insert--->" + e.message))
                println(e.message)
            }
        }
        return true
    }

    @GetMapping("pagingOriginalUrlTask")
    @RestfulPack
    fun pagingOriginalUrlTask(@PageableDefault(value = 20) pageable: Pageable): Page<PixivWork> {
        return pixivWorkService.paging(pageable)
    }

    @PostMapping("updateOriginalUrl")
    @RestfulPack
    fun updateOriginalUrl(@RequestBody dto: UpdateOriginalUrlDto): Boolean {
        try {
            val originalUrl = dto.originalUrl
            val work = pixivWorkService.getByPixivId(dto.pixivId!!)
            work.originalUrl = originalUrl
            work.translateTags = dto.translateTags
            work.description = EmojiUtil.emojiChange(dto.description ?: "").trim()
            if (originalUrl != null && originalUrl.startsWith("https://i.pximg.net/")) {
                val allUrlList = mutableListOf<String>()
                val proxyUrlList = mutableListOf<String>()
                for (i in 0 until work.pageCount) {
                    val pictureName = originalUrl.split("/").last()
                    val suitPictureName = pictureName.replace("p0", "p$i")
                    val suitUrl = originalUrl.replace(pictureName, suitPictureName)
                    pixivWorkDetailService.save(PixivWorkDetail(
                            work.pixivId!!,
                            pictureName,
                            suitUrl,
                            suitUrl.replace("https://i.pximg.net/",
                                    "https://pixiv.zeongit.workers.dev/"),
                            work.xRestrict,
                            work.pixivRestrict
                    ))
                    allUrlList.add(suitUrl)
                    proxyUrlList.add(suitUrl.replace("https://i.pximg.net/",
                            "https://pixiv.zeongit.workers.dev/"))
                }
                work.allUrl = allUrlList.joinToString("|")
                work.proxyUrl = proxyUrlList.joinToString("|")
            }
            pixivWorkService.save(work)
        } catch (e: Exception) {
            collectErrorService.save(CollectError(dto.pixivId ?: "", "updateOriginalUrl---->" + e.message))
            println(e.message)
        }
        return true
    }

    /**
     * 根据文件夹写入图片写入数据库
     * 本地使用
     */
    @PostMapping("checkDownload")
    @RestfulPack
    fun checkDownload(folderPath: String): Boolean {
        val pixivWorkList = pixivWorkService.listByDownload(false)
        val fileNameList = File(folderPath).list() ?: arrayOf()
        for (pixivWork in pixivWorkList) {
            pixivWork.download = fileNameList.toList().filter { it.toLowerCase().startsWith(pixivWork.pixivId!!) }.size == pixivWork.pageCount
            pixivWorkService.save(pixivWork)
        }
        return true
    }


    private fun initUser(nickname: String?): UserInfo {
        var phone = Random().nextInt(10)
        while (userService.existsByPhone(phone.toString())) {
            phone += Random().nextInt(1000)
        }
        val user = userService.signUp(phone.toString(), "123456")
        val gender = if (phone % 2 == 0) Gender.FEMALE else Gender.MALE
        val info = UserInfo(gender = gender, nickname = nickname ?: "镜花水月", introduction = nickname ?: "镜花水月")
        info.userId = user.id!!
        return userInfoService.save(info)
    }
}