package com.jaeyong.oop.boot.config

import com.jaeyong.oop.presentation.auth.interceptor.CurrentMemberArgumentResolver
import com.jaeyong.oop.presentation.auth.interceptor.JwtAuthenticationInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

// Spring MVC의 웹 관련 설정을 담당하는 클래스
// CORS, 인터셉터(인증 관문), ArgumentResolver(@CurrentMember) 3가지를 설정한다.
@Configuration
class WebConfig(
    @Value("\${cors.allowed-origins}") private val allowedOrigins: Array<String>, // application.yml에서 허용할 출처 목록을 읽어온다
    private val jwtAuthenticationInterceptor: JwtAuthenticationInterceptor,       // JWT 토큰 검증 인터셉터
    private val currentMemberArgumentResolver: CurrentMemberArgumentResolver,     // @CurrentMember 파라미터에 회원 ID를 주입하는 리졸버
) : WebMvcConfigurer {

    // ── CORS 설정 ──
    // 다른 도메인(예: localhost:3000 프론트엔드)에서 이 API를 호출할 수 있도록 허용한다.
    // 이 설정이 없으면 브라우저가 요청을 차단한다. (Postman/curl은 영향 없음)
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")                // 모든 경로에 대해 CORS 적용
            .allowedOrigins(*allowedOrigins)       // 허용할 출처 (localhost:3000, localhost:8080 등)
            .allowedMethods(                       // 허용할 HTTP 메서드
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.OPTIONS.name(),
            )
            .allowedHeaders("*")                   // 모든 요청 헤더 허용
            .allowCredentials(true)                 // 쿠키/인증정보 포함 허용
            .maxAge(3600)                           // 브라우저가 CORS 결과를 1시간 동안 캐싱 (매번 확인 안 해도 됨)
    }

    // ── 인터셉터 설정 ──
    // 모든 /api/** 요청 전에 JWT 토큰을 검사하는 관문을 세운다.
    // 비유: 건물 입구에 출입카드 리더기를 설치하되, 로비(로그인/회원가입 등)는 카드 없이 통과 가능.
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
            .addPathPatterns("/api/**")            // /api/로 시작하는 모든 요청에 JWT 검사 적용
            .excludePathPatterns(                  // 아래 경로는 토큰 없이 접근 가능 (인증 제외)
                "/api/v1/auth/signup",             // 회원가입 — 토큰이 아직 없는 사용자
                "/api/v1/auth/login",              // 로그인 — 토큰을 받으려는 사용자
                "/api/v1/auth/reissue",            // 토큰 재발급 — Access Token이 만료된 상태
                "/api/health/**",                  // 헬스체크 — 서버 상태 확인용
                "/swagger-ui/**",                  // Swagger UI 페이지
                "/v3/api-docs/**",                 // Swagger API 문서 데이터
            )
    }

    // ── ArgumentResolver 설정 ──
    // 컨트롤러에서 @CurrentMember memberId: Long 파라미터를 사용하면
    // 인터셉터가 토큰에서 꺼낸 회원 ID가 자동으로 주입된다.
    // 인터셉터(토큰 검증 → memberId 추출) → ArgumentResolver(memberId를 파라미터에 전달) 순으로 동작한다.
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(currentMemberArgumentResolver)
    }
}
