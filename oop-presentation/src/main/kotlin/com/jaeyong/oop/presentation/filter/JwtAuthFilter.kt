package com.jaeyong.oop.presentation.filter

import com.jaeyong.oop.application.user.usecase.TokenValidationUseCase
import com.jaeyong.oop.common.auth.AuthContext
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
        try {
            if (token != null) {
                val username = tokenValidationUseCase.validateAndExtract(token)
                if (username != null) {
                    AuthContext.set(username)
                }
            }
            filterChain.doFilter(request, response)
        } finally {
            AuthContext.clear()
        }
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearer = request.getHeader("Authorization") ?: return null
        return if (bearer.startsWith("Bearer ")) bearer.substring(7) else null
    }
}
