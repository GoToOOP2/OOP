package com.jaeyong.oop.application.post.result

import kotlin.ConsistentCopyVisibility

import com.jaeyong.oop.domain.post.Post

/**
 * 게시글 생성 유스케이스 결과.
 *
 * @property postId 생성된 게시글 ID
 */
@ConsistentCopyVisibility
data class CreatePostResult private constructor(val postId: Long) {
    companion object {
        /**
         * 저장된 Post 도메인 객체로부터 결과를 생성한다.
         */
        fun from(post: Post): CreatePostResult = CreatePostResult(post.getId())
    }
}
