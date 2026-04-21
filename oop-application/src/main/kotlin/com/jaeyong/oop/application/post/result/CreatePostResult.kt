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
        fun of(postId: Long): CreatePostResult = CreatePostResult(postId)

        /**
         * 저장된 Post 도메인 객체로부터 결과를 생성한다.
         */
        fun from(post: Post): CreatePostResult =
            CreatePostResult(requireNotNull(post.id) { "Post ID must not be null after save" }) // 게시판 생성했는데 PK가 생성 안된상황 -> 500 서버오류 (클라이언트 오류가 아님)
    }
}
