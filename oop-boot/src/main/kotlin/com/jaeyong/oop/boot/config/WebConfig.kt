package com.jaeyong.oop.boot.config

import com.jaeyong.oop.presentation.auth.CurrentUserArgumentResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    @Value("\${cors.allowed-origins}") private val allowedOrigins: Array<String>
) : WebMvcConfigurer {

    /**
     * [CurrentUserArgumentResolver]를 Spring MVC 인자 리졸버 목록에 등록한다.
     *
     * 이 설정으로 컨트롤러에서 `@CurrentUser` 파라미터를 사용할 수 있다.
     */
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(CurrentUserArgumentResolver())
    }

    /**
     * CORS 정책을 설정한다.
     *
     * 허용할 Origin은 `application.yml`의 `cors.allowed-origins`에서 주입받아 환경별로 다르게 적용한다.
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(*allowedOrigins)
            .allowedMethods(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.OPTIONS.name()
            )
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}
