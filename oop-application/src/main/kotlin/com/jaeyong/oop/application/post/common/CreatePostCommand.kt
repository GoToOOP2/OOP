package com.jaeyong.oop.application.post.common

import kotlin.ConsistentCopyVisibility

/**
 * 게시글 생성 커맨드.
 *
 * @property title 게시글 제목
 * @property content 게시글 내용
 * @property userId 작성자 ID
 */
@ConsistentCopyVisibility
data class CreatePostCommand private constructor(
    val title: String,
    val content: String,
    val userId: Long
) {
    companion object {
        fun of(title: String, content: String, userId: Long): CreatePostCommand =
            CreatePostCommand(title, content, userId)
    }
}
