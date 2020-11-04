package com.zeongit.admin.controller

import com.zeongit.admin.service.PictureService
import com.zeongit.admin.service.PixivWorkDetailService
import com.zeongit.data.constant.AspectRatio
import com.zeongit.data.constant.PictureLifeState
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.database.primary.entity.Picture
import com.zeongit.data.index.primary.document.PictureDocument
import com.zeongit.qiniu.core.component.QiniuConfig
import com.zeongit.qiniu.service.BucketService
import com.zeongit.share.annotations.RestfulPack
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("picture")
class PictureController(
        private val bucketService: BucketService,
        private val qiniuConfig: QiniuConfig,
        private val pictureService: PictureService,
        private val pixivWorkDetailService: PixivWorkDetailService,
        private val elasticsearchTemplate: ElasticsearchTemplate) {

    class IdListDto(var idList: List<Int>)


    @GetMapping("paging")
    @RestfulPack
    fun paging(pageable: Pageable, userId: Int?, name: String?, privacy: PrivacyState?, life: PictureLifeState?, master: Boolean?, startDate: Date?, endDate: Date?, aspectRatio: AspectRatio?): Page<Picture> {
        return pictureService.paging(pageable, userId, name, privacy, life, master, startDate, endDate, aspectRatio)
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
    @PostMapping("checkSize")
    @RestfulPack
    fun checkSize(): Boolean {
        for (picture in pictureService.list()) {
            val pixiv = pixivWorkDetailService.getByName(picture.url)
            picture.height = pixiv.height.toLong()
            picture.width = pixiv.width.toLong()

            picture.aspectRatio = when {
                picture.width > picture.height -> AspectRatio.HORIZONTAL
                picture.width < picture.height -> AspectRatio.VERTICAL
                else -> AspectRatio.SQUARE
            }
            pictureService.save(picture)
        }
        return true
    }


//    /**
//     * 获取套图
//     */
//    @GetMapping("listPictureBySuit")
//    @RestfulPack
//    fun listPictureBySuit(pixivId: String): List<Picture> {
//        val list = pixivPictureService.listByPixivId(pixivId)
//        val resultList = mutableListOf<Picture>()
//        for (item in list) {
//            try {
//                resultList.add(pictureService.getByLife(item.pictureId))
//            } catch (e: NotFoundException) {
//            }
//        }
//        return resultList
//    }
}
