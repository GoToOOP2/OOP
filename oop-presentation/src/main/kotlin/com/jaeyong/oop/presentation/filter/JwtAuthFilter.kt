package com.jaeyong.oop.presentation.filter

import com.jaeyong.oop.application.user.common.TokenValidationCommand
import com.jaeyong.oop.application.user.usecase.TokenValidationUseCase
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.filter.OncePerRequestFilter

/**
 * JWT 인증 필터 — 요청당 한 번 실행되며 Authorization 헤더의 access token을 검증한다.
 *
 * 검증된 username을 request attribute에 저장하면 [com.jaeyong.oop.presentation.auth.CurrentUserArgumentResolver]가
 * 컨트롤러 파라미터에 주입한다.
 * 인증 실패해도 filterChain은 계속 진행한다. 인가는 각 컨트롤러/서비스에서 처리한다.
 */
class JwtAuthFilter(
    private val tokenValidationUseCase: TokenValidationUseCase
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)
        if (token != null) {
            val result = tokenValidationUseCase.validateAndExtract(TokenValidationCommand.of(token))
            if (result.username != null) {
                request.setAttribute("username", result.username)
            }
        }
        filterChain.doFilter(request, response)
    }

    /**
     * Authorization 헤더에서 "Bearer " 접두사를 제거하고 순수 토큰 문자열을 반환한다.
     *
     * @param request HTTP 요청
     * @return 토큰 문자열, 헤더가 없거나 형식이 맞지 않으면 null
     */
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearer = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return if (bearer.startsWith("Bearer ")) bearer.substring(7) else null
    }
}
