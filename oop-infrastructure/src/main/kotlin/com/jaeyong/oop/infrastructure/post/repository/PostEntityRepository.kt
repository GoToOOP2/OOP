package com.jaeyong.oop.infrastructure.post.repository

import com.jaeyong.oop.infrastructure.post.entity.PostEntity

/** JPA/JOOQ 등 구현체 조립을 위한 추상화 인터페이스 */
interface PostEntityRepository {
    fun save(entity: PostEntity): PostEntity
    fun findPostEntityById(id: Long): PostEntity?
    fun findPageWithTotal(page: Int, size: Int, direction: String): Pair<List<PostEntity>, Long>
    fun count(): Long
    fun deleteById(id: Long)
}
