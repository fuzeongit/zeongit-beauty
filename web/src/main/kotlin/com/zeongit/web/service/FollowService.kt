package com.zeongit.web.service

import com.zeongit.data.constant.FollowState
import com.zeongit.data.database.primary.entity.Follow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * 用户的关注服务
 *
 * @author fjj
 */

interface FollowService {
    fun exists(followerId: Int?, followingId: Int): FollowState

    fun save(followingId: Int): Follow

    fun remove(followerId: Int, followingId: Int): Boolean

    fun pagingByFollowerId(followerId: Int, pageable: Pageable): Page<Follow>

    fun pagingByFollowingId(followingId: Int, pageable: Pageable): Page<Follow>

    fun listByFollowerId(followerId: Int): List<Follow>
}