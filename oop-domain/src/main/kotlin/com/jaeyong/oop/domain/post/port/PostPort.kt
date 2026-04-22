package com.jaeyong.oop.domain.post.port

import com.jaeyong.oop.domain.post.Post

interface PostPort {
    fun save(post: Post): Post
    fun getById(id: Long): Post?
    fun getAll(page: Int, size: Int): List<Post>
    fun countAll(): Long
    fun delete(id: Long)
}
