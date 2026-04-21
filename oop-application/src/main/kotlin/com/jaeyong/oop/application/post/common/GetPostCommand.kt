package com.jaeyong.oop.application.post.common

import kotlin.ConsistentCopyVisibility

/**
 * 게시글 단건 조회 커맨드.
 *
 * @property postId 조회할 게시글 ID
 */
@ConsistentCopyVisibility
data class GetPostCommand private constructor(
    val postId: Long
) {
    companion object {
        fun of(postId: Long): GetPostCommand =
            GetPostCommand(postId)
    }
}
