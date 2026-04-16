package com.jaeyong.oop.presentation.post.response

import java.time.LocalDateTime

data class PostDetailResponse private constructor(
    val id: Long,
    val title: String,
    val content: String,
    val authorId: Long,
    val authorName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun of(
            id: Long, title: String, content: String,
            authorId: Long, authorName: String,
            createdAt: LocalDateTime, updatedAt: LocalDateTime
        ): PostDetailResponse = PostDetailResponse(id, title, content, authorId, authorName, createdAt, updatedAt)
    }
}
