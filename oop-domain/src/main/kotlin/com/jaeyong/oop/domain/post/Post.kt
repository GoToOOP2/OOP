package com.jaeyong.oop.domain.post

import kotlin.ConsistentCopyVisibility

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.domain.post.vo.ContentVO
import com.jaeyong.oop.domain.post.vo.TitleVO
import java.time.LocalDateTime

/**
 * 게시글 도메인 객체.
 *
 * 외부에서 직접 생성 금지 — 반드시 [create], [restore] 팩토리 메서드를 통해 생성한다.
 */
@ConsistentCopyVisibility
data class Post private constructor(
    val id: Long? = null,
    var title: TitleVO,
    var content: ContentVO,
    val authorId: Long,
    var deleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {

    /**
     * 게시글을 수정한다.
     *
     * @param title 수정할 제목
     * @param content 수정할 내용
     * @param userId 요청자 ID
     * @throws BaseException 작성자가 아닌 경우 (ACCESS_DENIED)
     */
    fun update(title: String, content: String, userId: Long) {
        validateAuthor(userId)
        this.title = TitleVO.from(title)
        this.content = ContentVO.from(content)
        this.updatedAt = LocalDateTime.now()
    }

    /**
     * 게시글을 소프트 삭제한다.
     *
     * @param userId 요청자 ID
     * @throws BaseException 작성자가 아닌 경우 (ACCESS_DENIED)
     * @throws BaseException 이미 삭제된 경우 (INVALID_STATE)
     */
    fun delete(userId: Long) {
        validateAuthor(userId)
        if (deleted) {
            throw BaseException(ErrorCode.INVALID_STATE)
        }
        this.deleted = true
        this.updatedAt = LocalDateTime.now()
    }

    fun getId(): Long = id ?: throw BaseException(ErrorCode.INVALID_STATE)

    private fun validateAuthor(userId: Long) {
        if (authorId != userId) {
            throw BaseException(ErrorCode.ACCESS_DENIED)
        }
    }

    companion object {

        /**
         * 신규 게시글을 생성하고 저장한다.
         */
        fun create(title: String, content: String, authorId: Long, postPort: PostPort): Post {
            val post = Post(title = TitleVO.from(title), content = ContentVO.from(content), authorId = authorId)
            return postPort.store(post)
        }

        /**
         * DB에서 조회한 데이터로 Post를 복원한다.
         *
         * 비즈니스 규칙 검증 없이 순수 복원만 수행한다.
         */
        fun restore(
            id: Long,
            title: TitleVO,
            content: ContentVO,
            authorId: Long,
            deleted: Boolean,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Post = Post(id, title, content, authorId, deleted, createdAt, updatedAt)
    }
}
