package com.jaeyong.oop.presentation.auth.interceptor

import com.jaeyong.oop.application.usecase.TokenValidationUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

// 모든 API 요청이 컨트롤러에 도달하기 전에 JWT 토큰을 검사하는 인터셉터.
// WebConfig에서 등록되며, /api/** 경로에 적용된다. (회원가입/로그인 등 일부 경로는 제외)
// 검증 성공 시 토큰에서 꺼낸 memberId를 request에 저장하고,
// 이후 CurrentMemberArgumentResolver가 이 값을 컨트롤러 파라미터에 주입한다.
@Component
class JwtAuthenticationInterceptor(
    private val tokenValidationUseCase: TokenValidationUseCase, // 토큰 검증/파싱을 담당하는 유스케이스 (실제 구현은 application의 AuthService)
) : HandlerInterceptor {
    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"  // 클라이언트가 토큰을 보낼 때 사용하는 HTTP 헤더 이름
        private const val BEARER_PREFIX = "Bearer "               // 토큰 앞에 붙는 접두사. "Bearer eyJhbG..." 형식
        const val MEMBER_ID_ATTRIBUTE = "memberId"                // request에 회원 ID를 저장할 때 쓰는 키 이름
    }

    // Spring이 컨트롤러를 호출하기 직전에 자동으로 실행하는 메서드
    // true 반환 → 컨트롤러로 진행, 예외 발생 → 예외 핸들러로 이동
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        // 1. "Authorization" 헤더를 꺼낸다. 없으면 → 401 UNAUTHORIZED
        val header =
            request.getHeader(AUTHORIZATION_HEADER)
                ?: throw BaseException(ErrorCode.UNAUTHORIZED)

        // 2. "Bearer "로 시작하는지 확인한다. 아니면 → 401 UNAUTHORIZED
        if (!header.startsWith(BEARER_PREFIX)) {
            throw BaseException(ErrorCode.UNAUTHORIZED)
        }

        // 3. "Bearer eyJhbG..." 에서 "Bearer " 부분을 잘라내고 순수 토큰만 추출
        val token = header.substring(BEARER_PREFIX.length)

        // 4. 토큰 유효성 검증 (만료 → EXPIRED_TOKEN, 변조 → INVALID_TOKEN)
        tokenValidationUseCase.validateToken(token)

        // 5. 토큰에서 memberId를 꺼내서 request에 저장
        //    → 이후 CurrentMemberArgumentResolver가 이 값을 @CurrentMember 파라미터에 넣어준다
        val memberId = tokenValidationUseCase.extractMemberId(token)
        request.setAttribute(MEMBER_ID_ATTRIBUTE, memberId)

        return true
    }
}
