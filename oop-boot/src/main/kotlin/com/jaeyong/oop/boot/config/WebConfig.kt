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
     * [역할] 우리가 만든 리졸버를 Spring MVC의 공식 '배달부 명단'에 등록합니다.
     * 이 설정을 통해 Spring은 컨트롤러 실행 시 우리가 만든 리졸버를 인식하고 사용하게 됩니다.
     */
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        // 리졸버 목록에 추가(add)함으로써 Spring 시스템의 일부로 편입시킵니다.
        resolvers.add(CurrentUserArgumentResolver())
    }

    // 허용할 Origin은 application.yml의 cors.allowed-origins에서 주입받음 (환경별로 다르게 설정 가능)
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(*allowedOrigins) // 가변 인자로 펼쳐서 전달
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
