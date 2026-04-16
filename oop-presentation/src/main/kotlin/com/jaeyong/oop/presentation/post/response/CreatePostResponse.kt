package com.jaeyong.oop.presentation.post.response

import com.jaeyong.oop.application.post.result.CreatePostResult

/**
 * 게시글 생성 응답 DTO.
 *
 * @property postId 생성된 게시글 ID
 */
data class CreatePostResponse private constructor(val postId: Long) {
    companion object {
        /**
         * Application 결과를 응답 DTO로 변환한다.
         */
        fun from(result: CreatePostResult): CreatePostResponse =
            CreatePostResponse(result.postId)
    }
}
