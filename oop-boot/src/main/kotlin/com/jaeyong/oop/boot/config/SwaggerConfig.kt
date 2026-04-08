package com.jaeyong.oop.boot.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("OOP Project API")
                    .description("객체 지향 프로그래밍 프로젝트 API 문서")
                    .version("1.0.0"),
            )
    }
}
