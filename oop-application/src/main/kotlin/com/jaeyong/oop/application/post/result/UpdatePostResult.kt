package com.jaeyong.oop.application.post.result

import kotlin.ConsistentCopyVisibility

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.user.User
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
@ConsistentCopyVisibility
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

        /**
         * 수정된 Post 도메인 객체와 작성자로부터 결과를 생성한다.
         */
        fun from(post: Post, author: User): UpdatePostResult =
            UpdatePostResult(
                id = requireNotNull(post.id) { "Post ID must not be null after save" }, // 게시글 수정 후 PK가 없는 상황 → 500 서버오류 (클라이언트 오류가 아님)
                title = post.title.value,
                content = post.content.value,
                authorId = post.authorId,
                authorName = author.username.value,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt
            )
    }
}
