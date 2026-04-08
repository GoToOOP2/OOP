package com.jaeyong.oop.presentation.api.dto

import jakarta.validation.constraints.NotBlank

/**
 * 토큰 재발급 요청 DTO.
 * Access Token이 만료됐을 때, 클라이언트가 Refresh Token을 보내 새 토큰 쌍을 받는다.
 */
data class ReissueRequest(
    @field:NotBlank
    val refreshToken: String,
)
