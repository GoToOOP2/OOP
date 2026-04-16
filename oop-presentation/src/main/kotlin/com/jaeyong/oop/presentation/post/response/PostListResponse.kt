package com.jaeyong.oop.presentation.post.response

import java.time.LocalDateTime

/**
 * 게시글 목록 응답 DTO.
 *
 * @property id 게시글 ID
 * @property title 제목
 * @property authorId 작성자 ID
 * @property authorName 작성자 이름
 * @property createdAt 생성 일시
 */
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
