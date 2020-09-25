package com.zeongit.admin.service

import com.zeongit.data.constant.AspectRatio
import com.zeongit.data.constant.PictureLifeState
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.database.admin.entity.CollectError
import com.zeongit.data.index.primary.document.PictureDocument
import com.zeongit.data.database.primary.entity.Picture
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * 采集异常
 *
 * @author fjj
 */
interface CollectErrorService {
    fun save(collectError: CollectError): CollectError
}