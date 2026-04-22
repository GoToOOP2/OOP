package com.jaeyong.oop.presentation.post.response

import com.jaeyong.oop.application.post.result.CreatePostResult

data class CreatePostResponse(val id: Long) {
    companion object {
        fun of(result: CreatePostResult) = CreatePostResponse(result.id)
    }
}
