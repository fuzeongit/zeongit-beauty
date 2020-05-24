package com.junjie.admin.service

import com.junjie.data.index.primary.document.PictureDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface PictureDocumentService {
    fun get(id: Int): PictureDocument

    fun remove(id: Int): Boolean

    fun paging(pageable: Pageable, tagList: List<String>?, precise: Boolean, name: String?, startDate: Date?, endDate: Date?, userId: Int?, self: Boolean): Page<PictureDocument>

    fun countByTag(tag: String): Long

    fun getFirstByTag(tag: String): PictureDocument

    fun listTagTop30(): List<String>

    fun save(picture: PictureDocument): PictureDocument

    fun saveViewAmount(picture: PictureDocument, viewAmount: Long): PictureDocument

    fun saveLikeAmount(picture: PictureDocument, likeAmount: Long): PictureDocument

    fun saveAll(pictureList: List<PictureDocument>): MutableIterable<PictureDocument>
}