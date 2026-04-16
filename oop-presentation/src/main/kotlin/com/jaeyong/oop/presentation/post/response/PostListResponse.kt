package com.jaeyong.oop.presentation.post.response

import java.time.LocalDateTime

data class PostListResponse private constructor(
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
        ): PostListResponse = PostListResponse(id, title, authorId, authorName, createdAt)
    }
}
