package com.jaeyong.oop.application.post.common

import kotlin.ConsistentCopyVisibility

/**
 * 게시글 삭제 커맨드.
 *
 * @property postId 삭제할 게시글 ID
 * @property userId 삭제 요청자 ID
 */
@ConsistentCopyVisibility
data class DeletePostCommand private constructor(
    val postId: Long,
    val userId: Long
) {
    companion object {
        fun of(postId: Long, userId: Long): DeletePostCommand =
            DeletePostCommand(postId, userId)
    }
}