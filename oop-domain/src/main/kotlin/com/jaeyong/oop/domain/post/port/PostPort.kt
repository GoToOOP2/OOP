package com.jaeyong.oop.domain.post.port

import com.jaeyong.oop.domain.post.Post

interface PostPort {

    /**
     * 게시글을 저장하고 저장된 게시글을 반환한다.
     */
    fun save(post: Post): Post

    /**
     * 삭제되지 않은 게시글을 ID로 조회한다.
     */
    fun findByIdAndDeletedFalse(id: Long): Post?

    /**
     * 삭제되지 않은 모든 게시글을 조회한다.
     */
    fun findAllByDeletedFalse(): List<Post>
}
