package com.jaeyong.oop.infrastructure.post.repository

import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import com.jaeyong.oop.infrastructure.post.jpa.PostJpaRepository
import com.jaeyong.oop.infrastructure.post.jooq.PostJooqRepository
import org.springframework.stereotype.Repository

/** 
 * PostEntityRepository의 실제 구현체.
 * JPA와 JOOQ를 주입받아 상황에 맞게 위임(Delegation)한다.
 */
@Repository
class PostEntityRepositoryImpl(
    private val jpa: PostJpaRepository,
    private val jooq: PostJooqRepository
) : PostEntityRepository {

    override fun save(entity: PostEntity): PostEntity = jpa.save(entity)

    override fun findPostEntityById(id: Long): PostEntity? = jpa.findPostEntityById(id)

    override fun findPageWithTotal(page: Int, size: Int, direction: String): Pair<List<PostEntity>, Long> =
        jooq.findPageWithTotal(page, size, direction)

    override fun count(): Long = jpa.count()

    override fun deleteById(id: Long) = jpa.deleteById(id)
}
