package com.jaeyong.oop.application.post.common

data class UpdatePostCommand private constructor(
    val postId: Long,
    val title: String,
    val content: String,
    val requesterId: Long
) {
    companion object {
        fun of(postId: Long, title: String, content: String, requesterId: Long): UpdatePostCommand =
            UpdatePostCommand(postId, title, content, requesterId)
    }
}
