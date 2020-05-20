package com.junjie.data.index.primary.dao

import com.junjie.data.index.primary.document.PictureDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface PictureDocumentDAO : ElasticsearchRepository<PictureDocument, Int> {}