package com.jaeyong.oop.presentation.post.response

data class CreatePostResponse private constructor(val postId: Long) {
    companion object {
        fun of(postId: Long): CreatePostResponse = CreatePostResponse(postId)
    }
}
