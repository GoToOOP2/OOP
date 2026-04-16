package com.jaeyong.oop.application.post.result

import java.time.LocalDateTime

/**
 * 게시글 단건 조회 유스케이스 결과.
 *
 * @property id 게시글 ID
 * @property title 게시글 제목
 * @property content 게시글 내용
 * @property authorId 작성자 ID
 * @property authorName 작성자 이름
 * @property createdAt 생성 일시
 * @property updatedAt 수정 일시
 */
data class GetPostResult private constructor(
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
        ): GetPostResult = GetPostResult(id, title, content, authorId, authorName, createdAt, updatedAt)
    }
}
