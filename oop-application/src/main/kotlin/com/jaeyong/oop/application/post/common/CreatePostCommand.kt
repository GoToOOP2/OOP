package com.jaeyong.oop.application.post.common

import kotlin.ConsistentCopyVisibility

/**
 * 게시글 생성 커맨드.
 *
 * @property title 게시글 제목
 * @property content 게시글 내용
 * @property authorId 작성자 ID
 */
@ConsistentCopyVisibility
data class CreatePostCommand private constructor(
    val title: String,
    val content: String,
    val authorId: Long
) {
    companion object {
        fun of(title: String, content: String, authorId: Long): CreatePostCommand =
            CreatePostCommand(title, content, authorId)
    }
}
