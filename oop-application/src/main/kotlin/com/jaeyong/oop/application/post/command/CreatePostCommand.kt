package com.jaeyong.oop.application.post.command

data class CreatePostCommand private constructor(
    val title: String,
    val content: String,
    val authorUsername: String
) {
    companion object {
        fun of(title: String, content: String, authorUsername: String) =
            CreatePostCommand(title, content, authorUsername)
    }
}
