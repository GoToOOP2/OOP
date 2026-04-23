package com.jaeyong.oop.infrastructure.post.jpa

import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 게시글 JPA Repository — Spring Data JPA와 [PostEntityRepository]를 결합한다.
 */
interface PostJpaRepository : JpaRepository<PostJpaEntity, Long>, PostEntityRepository {
    override fun findByIdAndDeletedFalse(id: Long): PostJpaEntity?
    override fun findAllByDeletedFalse(): List<PostJpaEntity>
}
