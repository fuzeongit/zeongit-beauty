package com.zeongit.web.serviceimpl

import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.util.DateUtil
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.index.primary.dao.PictureDocumentDao
import com.zeongit.data.index.primary.document.PictureDocument
import com.zeongit.web.service.CollectionService
import com.zeongit.web.service.FollowService
import com.zeongit.web.service.FootprintService
import com.zeongit.web.service.PictureDocumentService
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms
import org.springframework.data.domain.*
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service
import java.util.*


@Service
class PictureDocumentServiceImpl(private val pictureDocumentDao: PictureDocumentDao,
                                 private val elasticsearchTemplate: ElasticsearchTemplate,
                                 private val collectionService: CollectionService,
                                 private val footprintService: FootprintService,
                                 private val followService: FollowService
) : PictureDocumentService {

    override fun get(id: Int): PictureDocument {
        return pictureDocumentDao.findById(id).orElseThrow { NotFoundException("图片不存在") }
    }

    override fun remove(id: Int): Boolean {
        try {
            pictureDocumentDao.deleteById(id)
            return true
        } catch (e: Exception) {
            throw e
        }
    }

    override fun paging(pageable: Pageable, tagList: List<String>?,
                        precise: Boolean, name: String?,
                        startDate: Date?, endDate: Date?,
                        userId: Int?, self: Boolean,
                        mustUserList: List<Int>?,
                        userBlacklist: List<Int>?, pictureBlacklist: List<Int>?): Page<PictureDocument> {
        val mustQuery = QueryBuilders.boolQuery()
        if (tagList != null && tagList.isNotEmpty()) {
            val tagBoolQuery = QueryBuilders.boolQuery()
            for (tag in tagList) {
                if (precise) {
                    tagBoolQuery.must(QueryBuilders.termQuery("tagList", tag))
                } else {
                    tagBoolQuery.should(QueryBuilders.wildcardQuery("tagList", "*$tag*"))
                }
            }
            mustQuery.must(tagBoolQuery)
        }
        if (!name.isNullOrEmpty())
            mustQuery.must(QueryBuilders.matchPhraseQuery("name", name))
        if (startDate != null || endDate != null) {
            val rangeQueryBuilder = QueryBuilders.rangeQuery("createDate")
            startDate?.let { rangeQueryBuilder.from(DateUtil.getDayBeginTime(it).time) }
            endDate?.let { rangeQueryBuilder.lte(DateUtil.getDayEndTime(it).time) }
            mustQuery.must(rangeQueryBuilder)
        }
        if (!self) {
            mustQuery.must(QueryBuilders.termQuery("privacy", PrivacyState.PUBLIC.toString()))
        }
        if (userId != null) {
            mustQuery.must(QueryBuilders.termQuery("createdBy", userId))
        }

        if (userId == null && mustUserList != null) {
            val boolQuery = QueryBuilders.boolQuery()
            for (item in mustUserList.toSet()) {
                boolQuery.should(QueryBuilders.termQuery("createdBy", item))
            }
            mustQuery.must(boolQuery)
        }

        if (userId == null && userBlacklist != null) {
            val userBlacklistBoolQuery = QueryBuilders.boolQuery()
            for (item in userBlacklist.toSet()) {
                userBlacklistBoolQuery.mustNot(QueryBuilders.termQuery("createdBy", item))
            }
            mustQuery.must(userBlacklistBoolQuery)
        }

        if (pictureBlacklist != null) {
            val pictureBlacklistBoolQuery = QueryBuilders.boolQuery()
            for (item in pictureBlacklist.toSet()) {
                pictureBlacklistBoolQuery.mustNot(QueryBuilders.termQuery("id", item))
            }
            mustQuery.must(pictureBlacklistBoolQuery)
        }

        return pictureDocumentDao.search(mustQuery, pageable)
    }

    override fun pagingByRecommend(pageable: Pageable, userId: Int?, startDate: Date?, endDate: Date?): Page<PictureDocument> {
        val tagList = mutableListOf<String>()
        val collectionPictureIdList = mutableListOf<Int>()
        if (userId != null) {
            val collectionList = collectionService.paging(PageRequest.of(0, 5), userId).content
            for (collection in collectionList) {
                collectionPictureIdList.add(collection.pictureId)
                try {
                    tagList.addAll(get(collection.pictureId).tagList)
                } catch (e: NotFoundException) {
                }
            }
        }
        return paging(pageable = pageable,
                tagList = tagList,
                startDate = startDate,
                endDate = startDate,
                pictureBlacklist = collectionPictureIdList)
    }

    override fun pagingRecommendById(pageable: Pageable, id: Int, startDate: Date?, endDate: Date?): Page<PictureDocument> {
        val tagList = try {
            get(id).tagList
        } catch (e: Exception) {
            listOf<String>()
        }
        return paging(pageable = pageable,
                tagList = tagList,
                startDate = startDate,
                endDate = startDate,
                pictureBlacklist = listOf(id))
    }

    override fun pagingByFollowing(pageable: Pageable, userId: Int, startDate: Date?, endDate: Date?): Page<PictureDocument> {
        val followingList = followService.listByFollowerId(userId)
        if (followingList.isEmpty()) {
            return PageImpl(listOf<PictureDocument>(), pageable, 0)
        } else {
            return paging(pageable = pageable,
                    startDate = startDate,
                    endDate = startDate,
                    mustUserList = followingList.map { it.followingId })
        }
    }

    override fun countByTag(tag: String): Long {
        val queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.termQuery("tagList", tag))
        val searchQuery = NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build()
        return elasticsearchTemplate.count(searchQuery, PictureDocument::class.java)
    }

    override fun getFirstByTag(tag: String): PictureDocument {
        return paging(
                PageRequest.of(0, 1, Sort(Sort.Direction.DESC, "likeAmount")),
                listOf(tag), true, null,
                null, null,
                null, false).content.first()
    }

    override fun listTagTop30(): List<String> {
        val aggregationBuilders = AggregationBuilders.terms("tagList").field("tagList").size(30).showTermDocCountError(true)
        val query = NativeSearchQueryBuilder()
                .withIndices("beauty_picture_search")
                .addAggregation(aggregationBuilders)
                .build()
        return elasticsearchTemplate.query(query) {
            it.aggregations.get<StringTerms>("tagList").buckets.map { bucket ->
                bucket.keyAsString
            }
        } ?: listOf()
    }

    override fun save(picture: PictureDocument): PictureDocument {
        val source = pictureDocumentDao.findById(picture.id).orElse(picture)
        picture.viewAmount = source.viewAmount
        picture.likeAmount = source.likeAmount
        return pictureDocumentDao.save(picture)
    }

    override fun saveViewAmount(picture: PictureDocument, viewAmount: Long): PictureDocument {
        picture.viewAmount = viewAmount
        return pictureDocumentDao.save(picture)
    }

    override fun saveLikeAmount(picture: PictureDocument, likeAmount: Long): PictureDocument {
        picture.likeAmount = likeAmount
        return pictureDocumentDao.save(picture)
    }

    override fun saveAll(pictureList: List<PictureDocument>): MutableIterable<PictureDocument> {
        return pictureDocumentDao.saveAll(pictureList.map {
            it.viewAmount = footprintService.countByPictureId(it.id)
            it.likeAmount = collectionService.countByPictureId(it.id)
            it
        })
    }

    override fun listByUserId(userId: Int): List<PictureDocument> {
        return pictureDocumentDao.findAllByCreatedBy(userId)
    }
}