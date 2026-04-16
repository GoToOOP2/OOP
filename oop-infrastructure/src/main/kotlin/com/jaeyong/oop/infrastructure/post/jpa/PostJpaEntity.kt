package com.jaeyong.oop.infrastructure.post.jpa

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.vo.ContentVO
import com.jaeyong.oop.domain.post.vo.TitleVO
import jakarta.persistence.*
import java.time.LocalDateTime

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

    fun toDomain(): Post = Post.restore(
        id = requireNotNull(id) { "Post entity ID must not be null" },
        title = TitleVO.from(title),
        content = ContentVO.from(content),
        authorId = authorId,
        deleted = deleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
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
