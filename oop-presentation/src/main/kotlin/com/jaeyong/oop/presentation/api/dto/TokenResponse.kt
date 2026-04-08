package com.jaeyong.oop.presentation.api.dto

/**
 * 토큰 응답 DTO.
 * 로그인 및 토큰 재발급 성공 시 클라이언트에게 반환된다.
 * - accessToken: API 호출 시 Authorization 헤더에 넣어 사용 (유효기간 30분)
 * - refreshToken: Access Token 만료 시 재발급 요청에 사용 (유효기간 14일)
 */
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
