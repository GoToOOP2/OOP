package com.jaeyong.oop.presentation.post.response

import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.UpdatePostResult
import java.time.LocalDateTime

data class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorUsername: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun of(result: CreatePostResult) = PostResponse(result.id, result.title, result.content, result.authorUsername, result.createdAt, null)
        fun of(result: GetPostResult) = PostResponse(result.id, result.title, result.content, result.authorUsername, result.createdAt, result.updatedAt)
        fun of(result: UpdatePostResult) = PostResponse(result.id, result.title, result.content, result.authorUsername, result.createdAt, result.updatedAt)
    }
}
