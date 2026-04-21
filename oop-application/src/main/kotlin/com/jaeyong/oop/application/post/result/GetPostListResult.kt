package com.jaeyong.oop.application.post.result

import kotlin.ConsistentCopyVisibility

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.user.User
import java.time.LocalDateTime

/**
 * 게시글 목록 조회 유스케이스 결과.
 *
 * @property id 게시글 ID
 * @property title 게시글 제목
 * @property authorId 작성자 ID
 * @property authorName 작성자 이름
 * @property createdAt 생성 일시
 */
@ConsistentCopyVisibility
data class GetPostListResult private constructor(
    val id: Long,
    val title: String,
    val authorId: Long,
    val authorName: String,
    val createdAt: LocalDateTime
) {
    companion object {
        private const val UNKNOWN_AUTHOR = "알 수 없음"

        fun of(
            id: Long, title: String,
            authorId: Long, authorName: String,
            createdAt: LocalDateTime
        ): GetPostListResult = GetPostListResult(id, title, authorId, authorName, createdAt)

        /**
         * Post 도메인 객체와 작성자로부터 결과를 생성한다.
         */
        fun from(post: Post, author: User?): GetPostListResult =
            GetPostListResult(
                id = requireNotNull(post.id) { "Post ID must not be null" }, //// 내가 만들어지는건 내가 검증한다, DB 조회된 게시글에 PK가 없는 상황 → 500 서버오류 (클라이언트 오류가 아님)
                title = post.title.value,
                authorId = post.authorId,
                authorName = author?.username?.value ?: UNKNOWN_AUTHOR,
                createdAt = post.createdAt
            )
    }
}
