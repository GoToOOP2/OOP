package com.jaeyong.oop.infrastructure.post.adapter

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.infrastructure.post.jpa.PostJpaEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.springframework.stereotype.Repository

@Repository
class PostPersistenceAdapter(
    private val postEntityRepository: PostEntityRepository
) : PostPort {

    override fun save(post: Post): Post =
        postEntityRepository.save(PostJpaEntity.fromDomain(post)).toDomain()

    override fun findByIdAndDeletedFalse(id: Long): Post? =
        postEntityRepository.findByIdAndDeletedFalse(id)?.toDomain()

    override fun findAllByDeletedFalse(): List<Post> =
        postEntityRepository.findAllByDeletedFalse().map { it.toDomain() }
}
