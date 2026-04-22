package com.jaeyong.oop.infrastructure.post.entity

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import jakarta.persistence.*
import java.time.LocalDateTime

/** 게시글 JPA 엔티티, toDomain/fromDomain으로 도메인 객체와 변환 */
@Entity
@Table(name = "posts")
class PostEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "content", nullable = false)
    val content: String,

    @Column(name = "author_username", nullable = false)
    val authorUsername: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null
) {
    fun toDomain(): Post = Post.restore(id, PostTitle.of(title), PostContent.of(content), authorUsername, createdAt, updatedAt)

    companion object {
        fun fromDomain(post: Post): PostEntity = PostEntity(
            id = post.id,
            title = post.titleValue,
            content = post.contentValue,
            authorUsername = post.authorUsername,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt
        )
    }
}
