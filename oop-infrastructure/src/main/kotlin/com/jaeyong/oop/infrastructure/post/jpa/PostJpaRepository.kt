package com.jaeyong.oop.infrastructure.post.jpa

import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostJpaRepository : JpaRepository<PostEntity, Long>, PostEntityRepository {

    @Query("SELECT p FROM PostEntity p WHERE p.id = :id")
    override fun findPostById(id: Long): PostEntity?

    @Query("SELECT p FROM PostEntity p ORDER BY p.createdAt DESC")
    override fun findAllPaged(pageable: Pageable): List<PostEntity>
}
