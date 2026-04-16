package com.jaeyong.oop.infrastructure.post.adapter

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.infrastructure.post.jpa.PostJpaEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.springframework.stereotype.Repository

/**
 * [PostPort] Outbound Adapter — JPA를 통해 게시글을 저장하고 조회한다.
 */
@Repository
class PostPersistenceAdapter(
    private val postEntityRepository: PostEntityRepository
) : PostPort {

    /**
     * 게시글을 저장하고 도메인 객체로 반환한다.
     *
     * @param post 저장할 게시글 도메인 객체
     * @return 저장된 게시글 도메인 객체
     */
    override fun save(post: Post): Post =
        postEntityRepository.save(PostJpaEntity.fromDomain(post)).toDomain()

    /**
     * ID로 삭제되지 않은 게시글을 조회한다.
     *
     * @param id 게시글 ID
     * @return 게시글 도메인 객체, 없으면 null
     */
    override fun findByIdAndDeletedFalse(id: Long): Post? =
        postEntityRepository.findByIdAndDeletedFalse(id)?.toDomain()

    /**
     * 삭제되지 않은 모든 게시글을 조회한다.
     *
     * @return 게시글 도메인 객체 목록
     */
    override fun findAllByDeletedFalse(): List<Post> =
        postEntityRepository.findAllByDeletedFalse().map { it.toDomain() }
}
