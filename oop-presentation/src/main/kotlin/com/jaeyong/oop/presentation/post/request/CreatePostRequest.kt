package com.jaeyong.oop.presentation.post.request

import com.jaeyong.oop.application.post.common.CreatePostCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 게시글 생성 요청 DTO.
 *
 * @property title 게시글 제목 (최대 255자)
 * @property content 게시글 본문
 */
data class CreatePostRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,

    @field:NotBlank
    val content: String
) {
    /**
     * 요청 DTO를 Application 레이어 커맨드로 변환한다.
     *
     * @param authorId 인증된 사용자 ID
     * @return 게시글 생성 커맨드
     */
    fun toCommand(authorId: Long): CreatePostCommand =
        CreatePostCommand.of(title = title, content = content, authorId = authorId)
}
