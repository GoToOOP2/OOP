package com.jaeyong.oop.application.post.common

data class CreatePostCommand private constructor(
    val title: String,
    val content: String,
    val authorId: Long
) {
    companion object {
        fun of(title: String, content: String, authorId: Long): CreatePostCommand =
            CreatePostCommand(title, content, authorId)
    }
}
