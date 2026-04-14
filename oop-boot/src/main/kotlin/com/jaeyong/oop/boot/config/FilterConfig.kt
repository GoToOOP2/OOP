package com.jaeyong.oop.boot.config

import com.jaeyong.oop.application.user.usecase.TokenValidationUseCase
import com.jaeyong.oop.presentation.filter.JwtAuthFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 서블릿 필터 등록 설정.
 */
@Configuration
class FilterConfig(
    private val tokenValidationUseCase: TokenValidationUseCase
) {

    /**
     * [JwtAuthFilter]를 `/api/` 하위 경로에 등록한다.
     *
     * swagger, actuator 등 `/api/` 외 경로는 JWT 검증 대상에서 제외된다.
     * order = 1 로 설정해 다른 필터보다 먼저 실행되도록 한다.
     */
    @Bean
    fun jwtAuthFilter(): FilterRegistrationBean<JwtAuthFilter> {
        val registration = FilterRegistrationBean(JwtAuthFilter(tokenValidationUseCase))
        registration.addUrlPatterns("/api/*")
        registration.order = 1
        return registration
    }
}
