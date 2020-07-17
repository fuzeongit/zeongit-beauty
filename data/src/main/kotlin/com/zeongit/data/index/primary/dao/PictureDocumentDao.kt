package com.zeongit.data.index.primary.dao

import com.zeongit.data.index.primary.document.PictureDocument
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface PictureDocumentDao : ElasticsearchRepository<PictureDocument, Int> {
    fun findAllByCreatedBy(createdBy: Int): List<PictureDocument>
}