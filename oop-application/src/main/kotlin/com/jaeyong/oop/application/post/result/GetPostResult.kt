package com.jaeyong.oop.application.post.result

import kotlin.ConsistentCopyVisibility

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.user.User
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
@ConsistentCopyVisibility
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

        /**
         * Post 도메인 객체와 작성자로부터 결과를 생성한다.
         */
        fun from(post: Post, author: User): GetPostResult =
            GetPostResult(
                id = requireNotNull(post.id) { "Post ID must not be null" }, // DB 조회된 게시글에 PK가 없는 상황 → 500 서버오류 (클라이언트 오류가 아님)
                title = post.title.value,
                content = post.content.value,
                authorId = post.authorId,
                authorName = author.username.value,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt
            )
    }
}
