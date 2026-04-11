package com.jaeyong.oop.presentation.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class JoinRequest(
    @Schema(description = "사용자 아이디 (4~50자)", example = "jaeyong")
    @field:NotBlank
    @field:Size(min = 4, max = 50)
    val username: String,

    @Schema(description = "비밀번호 (최소 4자)", example = "password123")
    @field:NotBlank
    @field:Size(min = 4)
    val password: String
)
