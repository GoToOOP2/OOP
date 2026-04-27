package com.jaeyong.oop.application.post.result

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post

data class CreatePostResult private constructor(
    val id: Long
) {
    companion object {
        fun from(post: Post) = CreatePostResult(
            post.id ?: throw BaseException(ErrorCode.SAVE_ID_NOT_RETURNED)
        )
    }
}
