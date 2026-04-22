package com.jaeyong.oop.infrastructure.post.jpa

import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import org.springframework.data.jpa.repository.JpaRepository

/** 오직 JPA 기능만 수행하는 인터페이스 */
interface PostJpaRepository : JpaRepository<PostEntity, Long> {
    fun findPostEntityById(id: Long): PostEntity?
}
