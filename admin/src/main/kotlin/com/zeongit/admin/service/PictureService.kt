package com.zeongit.admin.service

import com.zeongit.data.constant.PictureLifeState
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.constant.SizeType
import com.zeongit.data.index.primary.document.PictureDocument
import com.zeongit.data.database.primary.entity.Picture
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * 画的服务
 *
 * @author fjj
 */
interface PictureService {
    fun paging(pageable: Pageable, userId: Int?, name: String?, privacy: PrivacyState?, life: PictureLifeState?, master: Boolean?, startDate: Date?, endDate: Date?, sizeType: SizeType?): Page<Picture>

    fun get(id: Int): Picture

    fun getByLife(id: Int, life: PictureLifeState? = null): Picture

    /**
     * 逻辑删除
     */
    fun remove(picture: Picture): Boolean

    /**
     * 还原
     */
    fun reduction(id: Int): PictureDocument

    /**
     * 物理删除
     */
    fun delete(id: Int)

    fun list(): List<Picture>

    fun listByLife(life: PictureLifeState? = null): List<Picture>

    fun listByUserId(userId: Int): List<Picture>

    fun listByUserIdAndLife(userId: Int, life: PictureLifeState?): List<Picture>
    /**
     * @param force 是否强制更新
     */
    fun save(picture: Picture, force: Boolean = false): PictureDocument

    fun synchronizationIndexPicture(): Long
}