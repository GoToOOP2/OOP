package com.jaeyong.oop.application.result

/**
 * 로그인/토큰 재발급 유스케이스 출력 객체.
 * presentation의 TokenResponse(DTO)와 분리되어 있다.
 * 컨트롤러가 Result → Response 변환을 책임진다.
 *
 * - accessToken: API 호출 시 사용 (유효기간 30분)
 * - refreshToken: Access Token 만료 시 재발급에 사용 (유효기간 14일)
 */
data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
)
