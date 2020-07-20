package com.zeongit.web.controller

import com.zeongit.share.annotations.Auth
import com.zeongit.share.annotations.CurrentUserInfoId
import com.zeongit.share.annotations.RestfulPack
import com.zeongit.share.exception.NotFoundException
import com.zeongit.share.exception.PermissionException
import com.zeongit.share.exception.ProgramException
import com.zeongit.share.exception.SignInException
import com.zeongit.data.constant.CollectState
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.index.primary.document.PictureDocument
import com.zeongit.web.core.communal.PictureVoAbstract
import com.zeongit.web.service.CollectionService
import com.zeongit.web.service.FollowService
import com.zeongit.web.service.PictureDocumentService
import com.zeongit.web.service.UserInfoService
import com.zeongit.web.vo.CollectionPictureVo
import com.zeongit.web.vo.PictureVo
import com.zeongit.web.vo.UserInfoVo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author fjj
 * 作品
 */
@RestController
@RequestMapping("works")
class WorksController(override val pictureDocumentService: PictureDocumentService,
                      override val collectionService: CollectionService,
                      override val userInfoService: UserInfoService,
                      override val followService: FollowService) : PictureVoAbstract() {
    /**
     * 获取列表
     */
    @GetMapping("paging")
    @RestfulPack
    fun paging(@CurrentUserInfoId userId: Int?, @PageableDefault(value = 20) pageable: Pageable, targetId: Int?, startDate: Date?, endDate: Date?): Page<PictureVo> {
        (userId == null && targetId == null) && throw SignInException("请重新登录")
        return getPageVo(pictureDocumentService.paging(pageable, userId = targetId
                ?: userId!!, self = targetId == userId), userId)
    }

    private fun getPageVo(page: Page<PictureDocument>, userId: Int? = null): Page<PictureVo> {
        val pictureVoList = page.content.map { getPictureVo(it, userId) }
        return PageImpl(pictureVoList, page.pageable, page.totalElements)
    }
}