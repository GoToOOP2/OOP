package com.jaeyong.oop.application.usecase

/**
 * 토큰 검증 인바운드 포트 (UseCase).
 * presentation 레이어(JwtAuthenticationInterceptor)가 이 인터페이스를 호출하고,
 * application 레이어(AuthService)가 구현한다.
 *
 * presentation이 domain의 JwtPort를 직접 의존하지 않도록 중간 계층 역할을 한다.
 * 흐름: Interceptor → TokenValidationUseCase(application) → JwtPort(domain) ← JwtAdapter(infrastructure)
 */
interface TokenValidationUseCase {
    /**
     * JWT 토큰의 유효성을 검증한다.
     * 만료 → EXPIRED_TOKEN 예외, 변조/잘못된 형식 → INVALID_TOKEN 예외를 던진다.
     */
    fun validateToken(token: String)

    /** JWT 토큰의 subject(payload)에서 회원 ID를 추출한다. */
    fun extractMemberId(token: String): Long
}
