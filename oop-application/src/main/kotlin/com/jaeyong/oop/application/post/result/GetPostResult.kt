package com.jaeyong.oop.application.post.result

import com.jaeyong.oop.domain.post.Post
import java.time.LocalDateTime

data class GetPostResult private constructor(
    val id: Long,
    val title: String,
    val content: String,
    val authorUsername: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun of(post: Post) = GetPostResult(
            post.id!!, post.title.value, post.content.value,
            post.authorUsername, post.createdAt, post.updatedAt
        )
    }
}
