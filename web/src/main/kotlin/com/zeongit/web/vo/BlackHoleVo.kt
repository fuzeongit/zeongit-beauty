package com.zeongit.web.vo

import com.zeongit.data.constant.BlockState

class PictureBlackHoleVo(var id: Int, var url: String?, var name: String?, var state: BlockState)

class UserBlackHoleVo(var id: Int, var avatarUrl: String?, var nickname: String, var state: BlockState)

class TagBlackHoleVo(var tag: String, var state: BlockState)

class BlackHoleVo(var user: UserBlackHoleVo,
                  var tagList: List<TagBlackHoleVo>,
                  var picture: PictureBlackHoleVo? = null
)