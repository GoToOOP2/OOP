package com.jaeyong.oop.application.post.result

import java.time.LocalDateTime

data class GetPostListResult private constructor(
    val id: Long,
    val title: String,
    val authorId: Long,
    val authorName: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun of(
            id: Long, title: String,
            authorId: Long, authorName: String,
            createdAt: LocalDateTime
        ): GetPostListResult = GetPostListResult(id, title, authorId, authorName, createdAt)
    }
}
