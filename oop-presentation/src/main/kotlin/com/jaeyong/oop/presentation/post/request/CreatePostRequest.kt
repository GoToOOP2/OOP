package com.jaeyong.oop.presentation.post.request

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
)
