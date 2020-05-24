package com.junjie.data.database.primary.dao

import com.junjie.data.constant.PictureLifeState
import com.junjie.data.database.primary.entity.Picture
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.lang.Nullable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PictureDao : JpaRepository<Picture, Int>, JpaSpecificationExecutor<Picture> {
    @EntityGraph(value = "Picture.Tag", type = EntityGraph.EntityGraphType.FETCH)
    override fun findById(id: Int): Optional<Picture>

    @EntityGraph(value = "Picture.Tag", type = EntityGraph.EntityGraphType.FETCH)
    fun findByIdAndLife(id: Int, life: PictureLifeState): Optional<Picture>

    @EntityGraph(value = "Picture.Tag", type = EntityGraph.EntityGraphType.FETCH)
    fun findAllByLife(life: PictureLifeState): List<Picture>

    @EntityGraph(value = "Picture.Tag", type = EntityGraph.EntityGraphType.FETCH)
    fun findAllByLife(life: PictureLifeState, pageable: Pageable): Page<Picture>

    @EntityGraph(value = "Picture.Tag", type = EntityGraph.EntityGraphType.FETCH)
    override fun findAll(@Nullable specification: Specification<Picture>?, pageable: Pageable): Page<Picture>

    // @EntityGraph(value = "Picture.Tag", type = EntityGraph.EntityGraphType.FETCH)
    @Query(value = "SELECT * FROM picture ORDER BY RAND()", countQuery = "SELECT count(*) FROM picture", nativeQuery = true)
    fun pagingRand(pageable: Pageable): Page<Picture>

    fun findAllByCreatedBy(createdBy: Int): List<Picture>

    fun findAllByCreatedByAndLife(createdBy: Int, life: PictureLifeState): List<Picture>
}