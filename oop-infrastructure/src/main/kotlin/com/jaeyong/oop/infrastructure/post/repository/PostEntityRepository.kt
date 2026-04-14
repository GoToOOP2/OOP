package com.jaeyong.oop.infrastructure.post.repository

import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import org.springframework.data.domain.Pageable

interface PostEntityRepository {
    fun save(entity: PostEntity): PostEntity
    fun findPostById(id: Long): PostEntity?
    fun findAllPaged(pageable: Pageable): List<PostEntity>
    fun count(): Long
    fun deleteById(id: Long)
}
