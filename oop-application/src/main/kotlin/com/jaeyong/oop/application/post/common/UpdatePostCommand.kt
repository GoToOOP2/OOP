package com.jaeyong.oop.application.post.common

import kotlin.ConsistentCopyVisibility

/**
 * 게시글 수정 커맨드.
 *
 * @property postId 수정할 게시글 ID
 * @property title 수정할 제목
 * @property content 수정할 내용
 * @property requesterId 수정 요청자 ID
 */
@ConsistentCopyVisibility
data class UpdatePostCommand private constructor(
    val postId: Long,
    val title: String,
    val content: String,
    val requesterId: Long
) {
    companion object {
        fun of(postId: Long, title: String, content: String, requesterId: Long): UpdatePostCommand =
            UpdatePostCommand(postId, title, content, requesterId)
    }
}
