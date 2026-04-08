package com.jaeyong.oop.presentation.auth

/**
 * 컨트롤러 메서드 파라미터에 현재 인증된 회원의 ID를 주입하는 커스텀 어노테이션.
 *
 * 사용 예시:
 * ```
 * @PostMapping("/logout")
 * fun logout(@CurrentMember memberId: Long)
 * ```
 *
 * 동작 원리:
 * 1. JwtAuthenticationInterceptor가 토큰에서 memberId를 추출하여 request attribute에 저장
 * 2. CurrentMemberArgumentResolver가 이 어노테이션을 감지하여 attribute에서 memberId를 꺼내 파라미터에 주입
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CurrentMember
