package com.zeongit.web.serviceimpl

import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.util.DateUtil
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.index.primary.dao.PictureDocumentDao
import com.zeongit.data.index.primary.document.PictureDocument
import com.zeongit.web.core.component.ElasticsearchConfig
import com.zeongit.web.service.*
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.BucketOrder
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms
import org.springframework.data.domain.*
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service
import java.util.*


@Service
class PictureDocumentServiceImpl(
        private val elasticsearchConfig: ElasticsearchConfig,
        private val pictureDocumentDao: PictureDocumentDao,
        private val elasticsearchTemplate: ElasticsearchTemplate,
        private val collectionService: CollectionService,
        private val footprintService: FootprintService,
        private val followService: FollowService,
        private val userBlackHoleService: UserBlackHoleService,
        private val pictureBlackHoleService: PictureBlackHoleService,
        private val tagBlackHoleService: TagBlackHoleService
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
                        userBlacklist: List<Int>?, pictureBlacklist: List<Int>?, tagBlacklist: List<String>?): Page<PictureDocument> {
        val mustQuery = QueryBuilders.boolQuery()
        if (tagList != null) {
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

        if (tagBlacklist != null) {
            val tagBoolQuery = QueryBuilders.boolQuery()
            for (tag in tagBlacklist) {
                tagBoolQuery.mustNot(QueryBuilders.termQuery("tagList", tag))
            }
            mustQuery.must(tagBoolQuery)
        }
        return pictureDocumentDao.search(mustQuery, pageable)
    }

    override fun pagingByRecommend(pageable: Pageable, userId: Int?, startDate: Date?, endDate: Date?): Page<PictureDocument> {
        val tagList = mutableListOf<String>()
        val pictureBlacklist = pictureBlackHoleService.listBlacklist(userId)
        val userBlacklist = userBlackHoleService.listBlacklist(userId)
        if (userId != null) {
            collectionService.paging(PageRequest.of(0, 5), userId).content.forEach { collection ->
                //已收藏的图片加入排除列表
                pictureBlacklist.add(collection.pictureId)
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
                userBlacklist = userBlacklist,
                pictureBlacklist = pictureBlacklist,
                tagBlacklist = tagBlackHoleService.listBlacklist(userId)
        )
    }

    override fun pagingRecommendById(pageable: Pageable, id: Int, userId: Int?, startDate: Date?, endDate: Date?): Page<PictureDocument> {
        val userBlacklist = userBlackHoleService.listBlacklist(userId)
        val pictureBlacklist = pictureBlackHoleService.listBlacklist(userId)
        val tagList = try {
            get(id).tagList
        } catch (e: Exception) {
            listOf<String>()
        }
        pictureBlacklist.add(id)
        return paging(pageable = pageable,
                tagList = tagList,
                startDate = startDate,
                endDate = startDate,
                userBlacklist = userBlacklist,
                pictureBlacklist = pictureBlacklist,
                tagBlacklist = tagBlackHoleService.listBlacklist(userId)
        )
    }

    override fun pagingByFollowing(pageable: Pageable, userId: Int, startDate: Date?, endDate: Date?): Page<PictureDocument> {
        val followingList = followService.listByFollowerId(userId)
        if (followingList.isEmpty()) {
            return PageImpl(listOf<PictureDocument>(), pageable, 0)
        } else {
            return paging(pageable = pageable,
                    startDate = startDate,
                    endDate = startDate,
                    mustUserList = followingList.map { it.followingId },
                    pictureBlacklist = pictureBlackHoleService.listBlacklist(userId),
                    tagBlacklist = tagBlackHoleService.listBlacklist(userId)
            )
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

    override fun getFirstByTag(tag: String, userId: Int?): PictureDocument {
        return paging(
                PageRequest.of(0, 1, Sort(Sort.Direction.DESC, "likeAmount")),
                listOf(tag), true, null,
                null, null,
                null, false,
                listOf(),
                userBlackHoleService.listBlacklist(userId),
                pictureBlackHoleService.listBlacklist(userId),
                tagBlackHoleService.listBlacklist(userId)
        ).content.first()
    }

    override fun listTagTop30(userId: Int?): List<StringTerms.Bucket> {
        val tagBlacklist = tagBlackHoleService.listBlacklist(userId)
        val tagBoolQuery = QueryBuilders.boolQuery()
        tagBoolQuery.mustNot(QueryBuilders.termQuery("tagList", ""))
        for (tag in tagBlacklist) {
            tagBoolQuery.mustNot(QueryBuilders.termQuery("tagList", tag))
        }
        val aggregationBuilders = AggregationBuilders.terms("tagListCount").field("tagList").size(30).showTermDocCountError(true)
        val query = NativeSearchQueryBuilder()
                .withIndices(elasticsearchConfig.pictureSearch)
                .withQuery(tagBoolQuery)
                .addAggregation(aggregationBuilders)
                .build()
        return elasticsearchTemplate.query(query) {
            it.aggregations.get<StringTerms>("tagListCount").buckets
        }
    }

    override fun listTagByUserId(userId: Int): List<StringTerms.Bucket> {
        val mustQuery = QueryBuilders.boolQuery()
        mustQuery.must(QueryBuilders.termQuery("createdBy", userId))
        mustQuery.mustNot(QueryBuilders.termQuery("tagList", ""))
        val aggregationBuilders = AggregationBuilders.terms("tagListCount").field("tagList").size(30).showTermDocCountError(true)
        val query = NativeSearchQueryBuilder()
                .withIndices(elasticsearchConfig.pictureSearch)
                .withQuery(mustQuery)
                .addAggregation(aggregationBuilders)
                .build()
        return elasticsearchTemplate.query(query) {
            it.aggregations.get<StringTerms>("tagListCount").buckets
        }
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