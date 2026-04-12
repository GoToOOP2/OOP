package com.jaeyong.oop.presentation.user.response

import io.swagger.v3.oas.annotations.media.Schema

data class TokenResponse private constructor(
    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun of(accessToken: String, refreshToken: String = ""): TokenResponse = TokenResponse(accessToken, refreshToken)
    }
}
