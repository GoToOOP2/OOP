package com.jaeyong.oop.presentation.post.response

import com.jaeyong.oop.application.post.result.GetPostListResult
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
        /**
         * Application 결과를 응답 DTO로 변환한다.
         */
        fun from(result: GetPostListResult): PostListResponse =
            PostListResponse(result.id, result.title, result.authorId, result.authorName, result.createdAt)
    }
}
