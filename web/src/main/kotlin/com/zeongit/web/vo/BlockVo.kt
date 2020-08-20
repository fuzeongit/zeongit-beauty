package com.zeongit.web.vo

import com.zeongit.data.constant.BlockState

class PictureBlockVo(var id: Int, var url: String, var name: String, var state: BlockState)

class UserBlockVo(var id: Int, var avatarUrl: String?, var nickname: String, var state: BlockState)

class TagBlockVo(var tag: String, var state: BlockState)

class BlockVo(var user: UserBlockVo,
              var tagList: List<TagBlockVo>,
              var picture: PictureBlockVo? = null
)