package com.jaeyong.oop.infrastructure.post.jpa

import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.springframework.data.jpa.repository.JpaRepository

interface PostJpaRepository : JpaRepository<PostJpaEntity, Long>, PostEntityRepository {
    override fun findByIdAndDeletedFalse(id: Long): PostJpaEntity?
    override fun findAllByDeletedFalse(): List<PostJpaEntity>
}
