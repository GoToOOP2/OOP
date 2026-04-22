package com.jaeyong.oop.infrastructure.post.jpa

import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PostJpaRepository : JpaRepository<PostEntity, Long>, PostEntityRepository {
    override fun findPostEntityById(id: Long): PostEntity?
    override fun findAllByOrderByIdDesc(pageable: Pageable): List<PostEntity>
}
