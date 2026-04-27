package com.jaeyong.oop.presentation.post.response

import com.jaeyong.oop.application.post.result.UpdatePostResult
import java.time.LocalDateTime

/** 게시글 수정 응답 */
data class UpdatePostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorUsername: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(result: UpdatePostResult) = UpdatePostResponse(result.id, result.title, result.content, result.authorUsername, result.createdAt, result.updatedAt)
    }
}
