package com.jaeyong.oop.application.post.result

import com.jaeyong.oop.domain.post.Post
import java.time.LocalDateTime

data class UpdatePostResult private constructor(
    val id: Long,
    val title: String,
    val content: String,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun of(post: Post) = UpdatePostResult(post.id!!, post.title.value, post.content.value, post.updatedAt)
    }
}
