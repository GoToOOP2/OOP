package com.jaeyong.oop.infrastructure.post.adapter

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class PostPersistenceAdapter(
    private val postEntityRepository: PostEntityRepository
) : PostPort {

    override fun save(post: Post): Post =
        postEntityRepository.save(PostEntity.fromDomain(post)).toDomain()

    override fun findById(id: Long): Post? =
        postEntityRepository.findPostById(id)?.toDomain()

    override fun findAll(page: Int, size: Int): List<Post> =
        postEntityRepository.findAllPaged(PageRequest.of(page, size)).map { it.toDomain() }

    override fun countAll(): Long =
        postEntityRepository.count()

    override fun deleteById(id: Long) =
        postEntityRepository.deleteById(id)
}
