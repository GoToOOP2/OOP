package com.jaeyong.oop.presentation.post.response

import com.jaeyong.oop.application.post.result.GetPostResult
import java.time.LocalDateTime

/** 게시글 단건 조회 응답 */
data class GetPostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorUsername: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(result: GetPostResult) = GetPostResponse(result.id, result.title, result.content, result.authorUsername, result.createdAt, result.updatedAt)
    }
}
