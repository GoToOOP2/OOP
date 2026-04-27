package com.jaeyong.oop.boot.config

import com.jaeyong.oop.application.user.usecase.TokenValidationUseCase
import com.jaeyong.oop.presentation.filter.JwtAuthFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 서블릿 필터 등록 설정.
 */
@Configuration
@EnableConfigurationProperties(FilterProperties::class)
class FilterConfig(
    private val tokenValidationUseCase: TokenValidationUseCase,
    private val filterProperties: FilterProperties
) {

    /**
     * [JwtAuthFilter]를 설정된 URL 패턴에 등록한다.
     *
     * 적용 패턴은 `application.yml`의 `filter.url-patterns`로 관리한다.
     * order = 1 로 설정해 다른 필터보다 먼저 실행되도록 한다.
     */
    @Bean
    fun jwtAuthFilter(): FilterRegistrationBean<JwtAuthFilter> {
        val registration = FilterRegistrationBean(JwtAuthFilter(tokenValidationUseCase))
        registration.addUrlPatterns(*filterProperties.urlPatterns.toTypedArray())
        registration.order = 1
        return registration
    }
}
