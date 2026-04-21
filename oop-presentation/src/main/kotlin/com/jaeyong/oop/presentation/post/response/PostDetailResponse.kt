package com.jaeyong.oop.presentation.post.response

import kotlin.ConsistentCopyVisibility

import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.UpdatePostResult
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
@ConsistentCopyVisibility
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
        /**
         * 단건 조회 결과를 응답 DTO로 변환한다.
         */
        fun from(result: GetPostResult): PostDetailResponse =
            PostDetailResponse(result.id, result.title, result.content, result.authorId, result.authorName, result.createdAt, result.updatedAt)

        /**
         * 수정 결과를 응답 DTO로 변환한다.
         */
        fun from(result: UpdatePostResult): PostDetailResponse =
            PostDetailResponse(result.id, result.title, result.content, result.authorId, result.authorName, result.createdAt, result.updatedAt)
    }
}
