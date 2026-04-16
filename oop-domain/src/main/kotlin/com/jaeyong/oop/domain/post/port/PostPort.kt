package com.jaeyong.oop.domain.post.port

import com.jaeyong.oop.domain.post.Post

interface PostPort {
    fun save(post: Post): Post
    fun findById(id: Long): Post?
    fun findAll(page: Int, size: Int): List<Post>
    fun countAll(): Long
    fun deleteById(id: Long)
}
