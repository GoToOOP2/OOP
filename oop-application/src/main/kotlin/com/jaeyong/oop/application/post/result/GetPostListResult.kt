package com.jaeyong.oop.application.post.result

import kotlin.ConsistentCopyVisibility

import com.jaeyong.oop.domain.post.Post
import java.time.LocalDateTime

/**
 * 게시글 목록 조회 유스케이스 결과.
 *
 * @property id 게시글 ID
 * @property title 게시글 제목
 * @property createdAt 생성 일시
 */
@ConsistentCopyVisibility
data class GetPostListResult private constructor(
    val id: Long,
    val title: String,
    val createdAt: LocalDateTime
) {
    companion object {
        /**
         * Post 도메인 객체로부터 결과를 생성한다.
         */
        fun from(post: Post): GetPostListResult =
            GetPostListResult(
                id = post.getId(),
                title = post.title.value,
                createdAt = post.createdAt
            )
    }
}
