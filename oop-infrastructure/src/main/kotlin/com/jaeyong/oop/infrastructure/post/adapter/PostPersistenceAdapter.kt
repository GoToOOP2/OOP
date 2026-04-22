package com.jaeyong.oop.infrastructure.post.adapter

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.springframework.stereotype.Repository

/** PostPort 구현체, JPA와 JOOQ 하이브리드 엔진을 사용하여 데이터 처리 */
@Repository
class PostPersistenceAdapter(
    private val postEntityRepository: PostEntityRepository
) : PostPort {

    override fun store(post: Post): Post =
        postEntityRepository.save(PostEntity.fromDomain(post)).toDomain()

    override fun getById(id: Long): Post? =
        postEntityRepository.findPostEntityById(id)?.toDomain()

    override fun getAll(page: Int, size: Int, direction: String): List<Post> {
        val (entities, _) = postEntityRepository.findPageWithTotal(page, size, direction)
        return entities.map { it.toDomain() }
    }

    override fun countAll(): Long =
        postEntityRepository.count()

    override fun getAllWithTotal(page: Int, size: Int, direction: String): Pair<List<Post>, Long> {
        val (entities, total) = postEntityRepository.findPageWithTotal(page, size, direction)
        return entities.map { it.toDomain() } to total
    }

    override fun delete(id: Long) =
        postEntityRepository.deleteById(id)
}
