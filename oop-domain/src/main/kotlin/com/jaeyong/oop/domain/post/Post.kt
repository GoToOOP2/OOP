package com.jaeyong.oop.domain.post

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import java.time.LocalDateTime

/**
 * 게시글 도메인 엔티티
 *
 * - create: 신규 생성 / restore: DB 조회 후 복원 (createdAt 보존 목적)
 * - 불변 객체, 수정 시 새 객체로 대입
 */
data class Post private constructor(
    val id: Long? = null,
    val title: PostTitle,
    val content: PostContent,
    val authorUsername: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null
) {
    /** 작성자 본인 여부 검증, 실패 시 POST_ACCESS_DENIED */
    fun validateOwner(requesterUsername: String) {
        if (authorUsername != requesterUsername)
            throw BaseException(ErrorCode.POST_ACCESS_DENIED)
    }

    fun update(title: PostTitle, content: PostContent, requesterUsername: String): Post {
        validateOwner(requesterUsername)
        return copy(title = title, content = content, updatedAt = LocalDateTime.now())
    }

    companion object {
        fun create(title: PostTitle, content: PostContent, authorUsername: String): Post =
            Post(title = title, content = content, authorUsername = authorUsername, createdAt = LocalDateTime.now())

        fun restore(
            id: Long?,
            title: PostTitle,
            content: PostContent,
            authorUsername: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime?
        ): Post = Post(id, title, content, authorUsername, createdAt, updatedAt)
    }
}
