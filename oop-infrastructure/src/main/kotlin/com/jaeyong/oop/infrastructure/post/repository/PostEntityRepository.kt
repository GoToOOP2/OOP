package com.jaeyong.oop.infrastructure.post.repository

import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import org.springframework.data.domain.Pageable

/** JPA/MyBatis 등 구현체 교체를 위한 추상화 인터페이스 */
interface PostEntityRepository {
    fun save(entity: PostEntity): PostEntity
    fun findPostEntityById(id: Long): PostEntity?
    fun findAllByOrderByIdDesc(pageable: Pageable): List<PostEntity>
    fun count(): Long
    fun deleteById(id: Long)
}
