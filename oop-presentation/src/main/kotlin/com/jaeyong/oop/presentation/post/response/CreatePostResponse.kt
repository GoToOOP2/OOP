package com.jaeyong.oop.presentation.post.response

/**
 * 게시글 생성 응답 DTO.
 *
 * @property postId 생성된 게시글 ID
 */
data class CreatePostResponse private constructor(val postId: Long) {
    companion object {
        fun of(postId: Long): CreatePostResponse = CreatePostResponse(postId)
    }
}
