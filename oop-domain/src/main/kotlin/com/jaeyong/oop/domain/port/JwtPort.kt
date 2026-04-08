package com.jaeyong.oop.domain.port

/**
 * JWT 토큰 생성/검증 포트
 *
 * validateToken은 토큰이 유효하면 true, 만료되면 EXPIRED_TOKEN 예외, 변조/잘못된 형식이면 INVALID_TOKEN 예외를 던진다.
 */
interface JwtPort {
    fun createAccessToken(memberId: Long): String

    fun createRefreshToken(memberId: Long): String

    fun extractMemberId(token: String): Long

    fun validateToken(token: String)
}
