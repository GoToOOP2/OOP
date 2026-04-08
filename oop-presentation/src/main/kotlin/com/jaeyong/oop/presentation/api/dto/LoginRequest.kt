package com.jaeyong.oop.presentation.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * 로그인 요청 DTO.
 * 컨트롤러에서 @Valid로 검증한 뒤, LoginCommand로 변환하여 서비스에 전달한다.
 */
data class LoginRequest(
    @field:NotBlank
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
)
