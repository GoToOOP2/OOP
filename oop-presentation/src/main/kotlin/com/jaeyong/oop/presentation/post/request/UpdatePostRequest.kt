package com.jaeyong.oop.presentation.post.request

import com.jaeyong.oop.application.post.common.UpdatePostCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 게시글 수정 요청 DTO.
 *
 * @property title 수정할 제목 (최대 255자)
 * @property content 수정할 본문
 */
data class UpdatePostRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,

    @field:NotBlank
    val content: String
) {
    /**
     * 요청 DTO를 Application 레이어 커맨드로 변환한다.
     *
     * @param postId 수정할 게시글 ID
     * @param userId 인증된 사용자 ID
     * @return 게시글 수정 커맨드
     */
    fun toCommand(postId: Long, userId: Long): UpdatePostCommand =
        UpdatePostCommand.of(postId = postId, title = title, content = content, userId = userId)
}
