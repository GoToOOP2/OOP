package com.jaeyong.oop.application.post.result

data class CreatePostResult private constructor(val postId: Long) {
    companion object {
        fun of(postId: Long): CreatePostResult = CreatePostResult(postId)
    }
}
