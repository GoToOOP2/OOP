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
        registration.addUrlPatterns("/api/*")
        registration.order = 1
        return registration
    }
}
