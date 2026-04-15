package com.jaeyong.oop.application.post.common

data class DeletePostCommand private constructor(
    val postId: Long,
    val requesterId: Long
) {
    companion object {
        fun of(postId: Long, requesterId: Long): DeletePostCommand =
            DeletePostCommand(postId, requesterId)
    }
}
