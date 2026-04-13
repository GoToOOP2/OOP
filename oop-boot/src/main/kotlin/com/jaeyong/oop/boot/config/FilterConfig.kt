package com.jaeyong.oop.boot.config

import com.jaeyong.oop.application.user.usecase.TokenValidationUseCase
import com.jaeyong.oop.presentation.filter.JwtAuthFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig(
    private val tokenValidationUseCase: TokenValidationUseCase
) {

    @Bean
    fun jwtAuthFilter(): FilterRegistrationBean<JwtAuthFilter> {
        val registration = FilterRegistrationBean(JwtAuthFilter(tokenValidationUseCase))
        registration.addUrlPatterns("/api/*") // /api/ 하위 요청에만 JWT 검증 적용 (swagger, health 등 제외)
        registration.order = 1               // 여러 필터가 있을 때 실행 순서 (낮을수록 먼저)
        return registration
    }
}
