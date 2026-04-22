package com.jaeyong.oop.infrastructure.post.adapter

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

/** PostPort 구현체, PostEntityRepository 통해 JPA와 통신 */
@Repository
class PostPersistenceAdapter(
    private val postEntityRepository: PostEntityRepository
) : PostPort {

    override fun store(post: Post): Post =
        postEntityRepository.save(PostEntity.fromDomain(post)).toDomain()

    override fun getById(id: Long): Post? =
        postEntityRepository.findPostEntityById(id)?.toDomain()

    override fun getAll(page: Int, size: Int, direction: String): List<Post> {
        val sortOrder = Sort.by(Sort.Direction.fromString(direction), "id")
        return postEntityRepository.findAllByOrderByIdDesc(PageRequest.of(page, size, sortOrder)).map { it.toDomain() }
    }

    override fun countAll(): Long =
        postEntityRepository.count()

    override fun delete(id: Long) =
        postEntityRepository.deleteById(id)
}
