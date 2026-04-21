package com.jaeyong.oop.presentation.user.response

import kotlin.ConsistentCopyVisibility

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 로그인·토큰 갱신 응답 DTO.
 *
 * @property accessToken 발급된 JWT access token
 * @property refreshToken 발급된 JWT refresh token
 */
@ConsistentCopyVisibility
data class TokenResponse private constructor(
    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        /**
         * @param accessToken access token 문자열
         * @param refreshToken refresh token 문자열
         */
        fun of(accessToken: String, refreshToken: String = ""): TokenResponse = TokenResponse(accessToken, refreshToken)
    }
}
