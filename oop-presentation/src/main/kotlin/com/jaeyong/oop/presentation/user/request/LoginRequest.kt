package com.jaeyong.oop.presentation.user.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @Schema(description = "사용자 아이디", example = "jaeyong")
    @field:NotBlank
    val username: String,

    @Schema(description = "비밀번호", example = "password123")
    @field:NotBlank
    val password: String
)
