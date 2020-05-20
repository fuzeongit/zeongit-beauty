package com.junjie.web.serviceimpl

import com.junjie.data.constant.FollowState
import com.junjie.data.database.primary.dao.FollowDAO
import com.junjie.data.database.primary.entity.Follow
import com.junjie.web.service.FollowService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class FollowServiceImpl(private val followDAO: FollowDAO) : FollowService {
    override fun exists(followerId: Int?, followingId: Int): FollowState {
        if (followerId == null) {
            return FollowState.CONCERNED
        }
        if (followerId == followingId) {
            return FollowState.SElF
        }
        return try {
            if (followDAO.existsByCreatedByAndFollowingId(followerId, followingId)) FollowState.CONCERNED else FollowState.STRANGE
        } catch (e: Exception) {
            FollowState.SElF
        }
    }

    override fun save(followingId: Int): Follow {
        val follow = Follow(followingId)
        return followDAO.save(follow)
    }

    override fun remove(followerId: Int, followingId: Int): Boolean {
        return try {
            followDAO.deleteByCreatedByAndFollowingId(followerId, followingId)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override fun pagingByFollowerId(followerId: Int, pageable: Pageable): Page<Follow> {
        return followDAO.findAllByCreatedBy(followerId, pageable)
    }

    override fun pagingByFollowingId(followingId: Int, pageable: Pageable): Page<Follow> {
        return followDAO.findAllByFollowingId(followingId, pageable)
    }
}
