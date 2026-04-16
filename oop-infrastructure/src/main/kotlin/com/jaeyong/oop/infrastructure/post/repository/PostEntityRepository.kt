package com.jaeyong.oop.infrastructure.post.repository

import com.jaeyong.oop.infrastructure.post.jpa.PostJpaEntity

interface PostEntityRepository {

    fun save(entity: PostJpaEntity): PostJpaEntity

    fun findByIdAndDeletedFalse(id: Long): PostJpaEntity?

    fun findAllByDeletedFalse(): List<PostJpaEntity>
}
