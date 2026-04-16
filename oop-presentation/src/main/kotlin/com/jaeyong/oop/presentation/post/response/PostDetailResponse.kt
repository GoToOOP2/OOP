package com.jaeyong.oop.presentation.post.response

import java.time.LocalDateTime

/**
 * 게시글 상세 응답 DTO.
 *
 * @property id 게시글 ID
 * @property title 제목
 * @property content 본문
 * @property authorId 작성자 ID
 * @property authorName 작성자 이름
 * @property createdAt 생성 일시
 * @property updatedAt 수정 일시
 */
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
