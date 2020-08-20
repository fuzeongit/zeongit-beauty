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
               userBlacklist: List<Int>? = null, pictureBlacklist: List<Int>? = null, tagBlacklist: List<String>? = null
    ): Page<PictureDocument>

    fun pagingByRecommend(pageable: Pageable, userId: Int?, startDate: Date? = null, endDate: Date? = null): Page<PictureDocument>

    fun pagingByFollowing(pageable: Pageable, userId: Int, startDate: Date? = null, endDate: Date? = null): Page<PictureDocument>

    fun pagingRecommendById(pageable: Pageable, id: Int, userId: Int?, startDate: Date? = null, endDate: Date? = null): Page<PictureDocument>

    fun countByTag(tag: String): Long

    fun getFirstByTag(tag: String, userId: Int?): PictureDocument

    fun listTagTop30(): List<String>

    fun save(picture: PictureDocument): PictureDocument

    fun saveViewAmount(picture: PictureDocument, viewAmount: Long): PictureDocument

    fun saveLikeAmount(picture: PictureDocument, likeAmount: Long): PictureDocument

    fun saveAll(pictureList: List<PictureDocument>): MutableIterable<PictureDocument>

    fun listByUserId(userId: Int): List<PictureDocument>
}