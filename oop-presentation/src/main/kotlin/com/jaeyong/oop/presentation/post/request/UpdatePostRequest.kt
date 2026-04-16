package com.jaeyong.oop.presentation.post.request

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
)
