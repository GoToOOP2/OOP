package com.jaeyong.oop.application.post.result

import java.time.LocalDateTime

/**
 * 게시글 수정 유스케이스 결과.
 *
 * @property id 게시글 ID
 * @property title 수정된 제목
 * @property content 수정된 내용
 * @property authorId 작성자 ID
 * @property authorName 작성자 이름
 * @property createdAt 생성 일시
 * @property updatedAt 수정 일시
 */
data class UpdatePostResult private constructor(
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
        ): UpdatePostResult = UpdatePostResult(id, title, content, authorId, authorName, createdAt, updatedAt)
    }
}
