package com.jaeyong.oop.presentation.user.request

/**
 * 토큰 갱신 요청 DTO.
 *
 * @property refreshToken 갱신에 사용할 refresh token
 */
data class RefreshRequest(
    val refreshToken: String
)
