package com.jaeyong.oop.application.post.result

/**
 * 게시글 생성 유스케이스 결과.
 *
 * @property postId 생성된 게시글 ID
 */
data class CreatePostResult private constructor(val postId: Long) {
    companion object {
        fun of(postId: Long): CreatePostResult = CreatePostResult(postId)
    }
}
