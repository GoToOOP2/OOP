package com.jaeyong.oop.infrastructure.post.repository

import com.jaeyong.oop.infrastructure.post.jpa.PostJpaEntity

/**
 * 게시글 엔티티 저장소 인터페이스 — 인프라 레이어 내부의 Repository 추상화.
 */
interface PostEntityRepository {

    /**
     * 게시글 엔티티를 저장한다.
     *
     * @param entity 저장할 JPA 엔티티
     * @return 저장된 JPA 엔티티
     */
    fun save(entity: PostJpaEntity): PostJpaEntity

    /**
     * ID로 삭제되지 않은 게시글 엔티티를 조회한다.
     *
     * @param id 게시글 ID
     * @return JPA 엔티티, 없으면 null
     */
    fun findByIdAndDeletedFalse(id: Long): PostJpaEntity?

    /**
     * 삭제되지 않은 모든 게시글 엔티티를 조회한다.
     *
     * @return JPA 엔티티 목록
     */
    fun findAllByDeletedFalse(): List<PostJpaEntity>
}
