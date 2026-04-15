package com.jaeyong.oop.application.post.result

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post
import java.time.LocalDateTime

data class CreatePostResult private constructor(
    val id: Long,
    val title: String,
    val content: String,
    val authorUsername: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun of(post: Post) = CreatePostResult(
            post.id ?: throw BaseException(ErrorCode.SAVE_ID_NOT_RETURNED),
            post.title.value, post.content.value, post.authorUsername, post.createdAt
        )
    }
}
