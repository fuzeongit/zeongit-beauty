package com.zeongit.web.service

import com.zeongit.data.index.primary.document.PictureDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface PictureDocumentService {
    fun get(id: Int): PictureDocument

    fun remove(id: Int): Boolean

    fun paging(pageable: Pageable, tagList: List<String>? = null,
               precise: Boolean = false, name: String? = null,
               startDate: Date? = null, endDate: Date? = null,
               userId: Int? = null, self: Boolean = false,
               mustUserList: List<Int>? = null,
               userBlacklist: List<Int>? = null, pictureBlacklist: List<Int>? = null
    ): Page<PictureDocument>

    fun pagingByRecommend(userId: Int?, pageable: Pageable, startDate: Date? = null, endDate: Date? = null): Page<PictureDocument>

    fun pagingByFollowing(userId: Int, pageable: Pageable, startDate: Date? = null, endDate: Date? = null): Page<PictureDocument>

    fun pagingRecommendById(id: Int, pageable: Pageable, startDate: Date? = null, endDate: Date? = null): Page<PictureDocument>

    fun countByTag(tag: String): Long

    fun getFirstByTag(tag: String): PictureDocument

    fun listTagTop30(): List<String>

    fun save(picture: PictureDocument): PictureDocument

    fun saveViewAmount(picture: PictureDocument, viewAmount: Long): PictureDocument

    fun saveLikeAmount(picture: PictureDocument, likeAmount: Long): PictureDocument

    fun saveAll(pictureList: List<PictureDocument>): MutableIterable<PictureDocument>

    fun listByUserId(userId: Int): List<PictureDocument>
}