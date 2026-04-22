package com.jaeyong.oop.presentation.post.response

import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.UpdatePostResult
import java.time.LocalDateTime

/** 게시글 단건 응답, 조회/수정 Result를 공통 응답으로 변환 */
data class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorUsername: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(result: GetPostResult) = PostResponse(result.id, result.title, result.content, result.authorUsername, result.createdAt, result.updatedAt)
        fun from(result: UpdatePostResult) = PostResponse(result.id, result.title, result.content, result.authorUsername, result.createdAt, result.updatedAt)
    }
}
