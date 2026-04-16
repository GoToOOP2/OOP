package com.jaeyong.oop.infrastructure.post.jpa

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.vo.ContentVO
import com.jaeyong.oop.domain.post.vo.TitleVO
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 게시글 JPA 엔티티 — `posts` 테이블과 매핑되며, 도메인 객체와의 변환을 담당한다.
 */
@Entity
@Table(name = "posts")
class PostJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title", nullable = false, length = 255)
    val title: String,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Column(name = "author_id", nullable = false)
    val authorId: Long,

    @Column(name = "deleted", nullable = false)
    val deleted: Boolean = false,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {

    /**
     * JPA 엔티티를 도메인 객체로 변환한다.
     *
     * @return 게시글 도메인 객체
     */
    fun toDomain(): Post = Post.restore(
        id = requireNotNull(id) { "Post entity ID must not be null" }, // DB에서 조회된 엔티티에 PK가 없는 상황 → 500 서버오류 (클라이언트 오류가 아님)
        title = TitleVO.from(title),
        content = ContentVO.from(content),
        authorId = authorId,
        deleted = deleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        /**
         * 도메인 객체를 JPA 엔티티로 변환한다.
         *
         * @param post 게시글 도메인 객체
         * @return JPA 엔티티
         */
        fun fromDomain(post: Post): PostJpaEntity = PostJpaEntity(
            id = post.id,
            title = post.title.value,
            content = post.content.value,
            authorId = post.authorId,
            deleted = post.deleted,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt
        )
    }
}
