package com.jaeyong.oop.presentation.filter

import com.jaeyong.oop.application.user.auth.TokenValidationUseCase
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
            val username = tokenValidationUseCase.validateAndExtract(token)
            if (username != null) {
                request.setAttribute("username", username)
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearer = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return if (bearer.startsWith("Bearer ")) bearer.substring(7) else null
    }
}
