package com.jaeyong.oop.presentation.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * 회원가입 요청 DTO.
 * 컨트롤러에서 @Valid로 검증한 뒤, SignupCommand로 변환하여 서비스에 전달한다.
 */
data class SignupRequest(
    @field:NotBlank
    @field:Email // 이메일 형식 검증 (예: user@example.com)
    val email: String,
    @field:NotBlank
    @field:Size(min = 8) // 최소 8자
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).+$", message = "영문과 숫자를 포함해야 합니다")
    val password: String,
    @field:NotBlank
    @field:Size(min = 2, max = 20) // 2~20자
    val nickname: String,
)
