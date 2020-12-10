package com.zeongit.admin.controller

import com.zeongit.admin.dto.CollectDto
import com.zeongit.admin.dto.UpdateOriginalUrlDto
import com.zeongit.admin.service.*
import com.zeongit.data.constant.AspectRatio
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.database.admin.entity.CollectError
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
import com.zeongit.share.exception.ProgramException
import com.zeongit.share.util.EmojiUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
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
        private val pixivUserService: PixivUserService,
        private val pixivWorkService: PixivWorkService,
        private val pixivWorkDetailService: PixivWorkDetailService,
        private val collectErrorService: CollectErrorService,
        private val nsfwLevelService: NsfwLevelService,
        private val bucketService: BucketService,
        private val qiniuConfig: QiniuConfig
) {
    /**
     * 采集器采集到数据库
     */
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

    /**
     * 获取采集任务
     */
    @GetMapping("pagingOriginalUrlTask")
    @RestfulPack
    fun pagingOriginalUrlTask(@PageableDefault(value = 20) pageable: Pageable): Page<PixivWork> {
        return pixivWorkService.paging(pageable)
    }

    /**
     * 更新原始数据
     */
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
                            suitPictureName,
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
        val pixivWorkDetailList = pixivWorkDetailService.listByDownload(false)
//        val pixivWorkDetailList = pixivWorkDetailService.listByWidth(0)

        for (pixivWorkDetail in pixivWorkDetailList) {
            pixivWorkDetail.download = fileNameList.toList().contains(pixivWorkDetail.name)
            try {
                if (pixivWorkDetail.download) {
                    val read = ImageIO.read(FileInputStream(File("$folderPath/${pixivWorkDetail.name}")))
                    pixivWorkDetail.width = read.width
                    pixivWorkDetail.height = read.height
                    pixivWorkDetailService.save(pixivWorkDetail)
                }
            } catch (e: Exception) {
            }
        }
        return true
    }

    /**
     * 根据文件夹写入图片写入数据库
     * 本地使用
     */
    @PostMapping("checkUse")
    @RestfulPack
    fun checkUse(folderPath: String, userId: Int, privacy: PrivacyState): Boolean {
        val fileNameList = File(folderPath).list() ?: arrayOf()
        println(fileNameList.size)
        for (fileName in fileNameList) {
            try {
                //获取pixiv图片详情
                val pixivWorkDetail = try {
                    val pixivWorkDetail = try {
                        pixivWorkDetailService.getByName(fileName)
                    } catch (e: NotFoundException) {
                        PixivWorkDetail(fileName.split("_").first(), fileName, "hide", "hide", 0, 0)
                    }
                    pixivWorkDetail.using = true
                    pixivWorkDetailService.save(pixivWorkDetail)
                    pixivWorkDetail
                } catch (e: Exception) {
                    throw ProgramException("$fileName---------上半部错误")
                }
                //现在数据库获取图片信息，如果没有直接读取图片信息
                val pixivWork = try {
                    pixivWorkService.getByPixivId(pixivWorkDetail.pixivId!!)
                } catch (e: NotFoundException) {
                    val read = ImageIO.read(FileInputStream(File("$folderPath/$fileName")))
                    val vo = PixivWork()
                    vo.width = read.width
                    vo.height = read.height
                    vo
                }

                //根据url获取正式数据库图片信息
                val picture = try {
                    pictureService.getByUrl(fileName)
                } catch (e: NotFoundException) {
                    Picture(
                            fileName,
                            pixivWorkDetail.width.toLong(),
                            pixivWorkDetail.height.toLong(),
                            pixivWork.title,
                            pixivWork.description,
                            privacy)
                }
                //获取pixiv用户信息
                val info = try {
                    if (pixivWork.userId == null) {
                        userInfoService.get(userId)
                    } else {
                        val pixivUser = pixivUserService.getByPixivUserId(pixivWork.userId!!)
                        userInfoService.get(pixivUser.userId)
                    }
                } catch (e: NotFoundException) {
                    //都失败创建一个用户
                    val info = initUser(pixivWork.userName)
                    if (pixivWork.userId != null) {
                        pixivUserService.save(info.id!!, pixivWork.userId!!)
                    }
                    info
                }
                picture.createdBy = info.id!!
                picture.lastModifiedBy = info.id!!
                val translateTags = pixivWork.translateTags ?: ""
                if (translateTags.isNotBlank()) {
                    picture.tagList = translateTags.split("|").toSet().asSequence().map {
                        val tag = Tag(it)
                        tag.createdBy = info.id!!
                        tag.lastModifiedBy = info.id!!
                        tag
                    }.toMutableSet()
                }
                pictureService.save(picture, true)
            } catch (e: Exception) {
                println(fileName)
                println(e.message)
            }
        }
        return true
    }

    @PostMapping("checkRestrict")
    @RestfulPack
    fun checkRestrict(sourcePath: String, folderPath: String): List<String> {
        val sourcePathList = File(sourcePath).list() ?: arrayOf()
        val list = mutableListOf<String>()
        for (path in sourcePathList) {
            val detail = try {
                pixivWorkDetailService.getByName(path)
            } catch (e: NotFoundException) {
                list.add(path)
                continue
            }
            if (detail.xRestrict == 1) {
                Files.move(Paths.get("$sourcePath/$path"), Paths.get("$folderPath/$path"))
            }
        }
        return list
    }

    /**
     * 将未下载的输出txt
     */
    @GetMapping("toTxt")
    @RestfulPack
    fun toTxt() {
        writeTxt("D:\\my\\图片\\p\\download_proxy.txt",
                pixivWorkDetailService.listByDownload(false).joinToString("\r\n") { it.proxyUrl })
        writeTxt("D:\\my\\图片\\p\\download.txt",
                pixivWorkDetailService.listByDownload(false).joinToString("\r\n") { it.url })
    }

    /**
     * 破图再次输出下载txt
     */
    @PostMapping("toTxtAgain")
    @RestfulPack
    fun toTxtAgain(sourcePath: String) {
        val sourcePathList = File(sourcePath).list() ?: arrayOf()
        val list = mutableListOf<PixivWorkDetail>()
        for (path in sourcePathList) {
            try {
                list.add(pixivWorkDetailService.getByName(path))
            } catch (e: NotFoundException) {
                continue
            }
        }
        writeTxt("D:\\my\\图片\\p\\download_proxy.txt",
                list.joinToString("\r\n") { it.proxyUrl })
        writeTxt("D:\\my\\图片\\p\\download.txt",
                list.joinToString("\r\n") { it.url })
    }

    @PostMapping("checkErrorPicture")
    @RestfulPack
    fun checkErrorPicture(folderPath: String) {
        val pictureList = pictureService.list()
        for (picture in pictureList) {
            if (picture.width == 0L || picture.height == 0L) {
                try {
                    val read = ImageIO.read(FileInputStream(File("$folderPath/${picture.url}")))
                    val pixivworkDetail = pixivWorkDetailService.getByName(picture.url)
                    pixivworkDetail.height = read.height
                    pixivworkDetail.width = read.width
                    picture.height = read.height.toLong()
                    picture.width = read.width.toLong()
                    picture.aspectRatio = when {
                        picture.width > picture.height -> AspectRatio.HORIZONTAL
                        picture.width < picture.height -> AspectRatio.VERTICAL
                        else -> AspectRatio.SQUARE
                    }
                    pixivWorkDetailService.save(pixivworkDetail)
                    pictureService.save(picture)
                } catch (e: Exception) {
                    println(e.message)
                    println(picture.id)
                    println("--------------------------------")
                }
            }
        }
    }

    @PostMapping("move")
    @RestfulPack
    fun move(sourcePath: String, folderPath: String): Boolean {
        val list = nsfwLevelService.list()

        for (nsfwLevel in list) {
            try {
                Files.move(Paths.get("$sourcePath/${nsfwLevel.url}"), Paths.get("$folderPath/${nsfwLevel.classify}/${nsfwLevel.url}"))
            } catch (e: Exception) {
                print(e)
            }
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

    private fun writeTxt(txtPath: String, content: String) {
        val fileOutputStream: FileOutputStream
        val file = File(txtPath)
        try {
            if (file.exists()) {
                //判断文件是否存在，如果不存在就新建一个txt
                file.createNewFile()
            }
            fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun readTxt(txtPath: String): String? {
        val file = File(txtPath)
        return if (file.isFile && file.exists()) {
            try {
                val fileInputStream = FileInputStream(file)
                val inputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val sb = StringBuffer()
                var text: String?
                while (bufferedReader.readLine().also { text = it } != null) {
                    sb.append(text)
                }
                sb.toString()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                throw e
            }
        } else {
            null
        }
    }
}