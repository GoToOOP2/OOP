package com.jaeyong.oop.application.usecase

import com.jaeyong.oop.application.command.LoginCommand
import com.jaeyong.oop.application.command.SignupCommand
import com.jaeyong.oop.application.result.TokenResult

/**
 * 인증 관련 인바운드 포트 (UseCase).
 * presentation 레이어(AuthController)가 이 인터페이스를 호출하고,
 * application 레이어(AuthService)가 구현한다.
 *
 * 입력은 Command 객체(command 패키지), 출력은 Result 객체(result 패키지)로 분리하여
 * 행위 계약(UseCase)과 데이터 구조(Command/Result)의 책임을 나눈다.
 */
interface AuthUseCase {
    /** 회원가입: 이메일 중복 검사 → 비밀번호 해싱 → 회원 저장 */
    fun signup(command: SignupCommand)

    /** 로그인: 이메일/비밀번호 검증 → Access/Refresh Token 발급 → Refresh Token DB 저장 */
    fun login(command: LoginCommand): TokenResult

    /** 토큰 재발급: Refresh Token 검증 → 새 Access/Refresh Token 발급 (토큰 로테이션) */
    fun reissue(refreshToken: String): TokenResult

    /** 로그아웃: DB에 저장된 Refresh Token을 삭제하여 재발급을 차단한다. */
    fun logout(memberId: Long)
}
