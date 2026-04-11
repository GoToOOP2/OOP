package com.jaeyong.oop.presentation.user

import io.swagger.v3.oas.annotations.media.Schema

data class TokenResponse(
    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    val accessToken: String
)
