package com.junjie.admin.serviceimpl

import com.junjie.core.exception.NotFoundException
import com.junjie.core.util.DateUtil
import com.junjie.data.constant.PrivacyState
import com.junjie.data.index.primary.dao.PictureDocumentDao
import com.junjie.data.index.primary.document.PictureDocument
import com.junjie.admin.service.PictureDocumentService
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service
import java.util.*


@Service
class PictureDocumentServiceImpl(private val pictureDocumentDao: PictureDocumentDao,
                                 private val elasticsearchTemplate: ElasticsearchTemplate) : PictureDocumentService {
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

    override fun paging(pageable: Pageable, tagList: List<String>?, precise: Boolean, name: String?, startDate: Date?, endDate: Date?, userId: Int?, self: Boolean): Page<PictureDocument> {
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
            mustQuery.must(QueryBuilders.termQuery("userId", userId))
        }
        return pictureDocumentDao.search(mustQuery, pageable)
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
            it.viewAmount = 0
            it.likeAmount = 0
            it
        })
    }
}