package com.jaeyong.oop.presentation.filter

import com.jaeyong.oop.application.user.common.TokenValidationCommand
import com.jaeyong.oop.application.user.usecase.TokenValidationUseCase
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.filter.OncePerRequestFilter

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
                // 검증된 username을 request에 저장 → CurrentUserArgumentResolver가 꺼내서 컨트롤러 파라미터에 주입
                request.setAttribute("username", result.username)
            }
        }
        // 인증 실패해도 filterChain은 계속 진행 (인가는 각 컨트롤러/서비스에서 처리)
        filterChain.doFilter(request, response)
    }

    // Authorization 헤더에서 "Bearer " 접두사를 제거하고 순수 토큰 문자열만 반환
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearer = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return if (bearer.startsWith("Bearer ")) bearer.substring(7) else null
    }
}
