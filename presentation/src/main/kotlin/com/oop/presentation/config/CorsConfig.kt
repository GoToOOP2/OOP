package com.oop.presentation.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig(
    @Value("\${cors.allowed-origins}") private val allowedOrigins: String
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        val mapping = registry.addMapping("/**")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")

        if (allowedOrigins == "*") {
            mapping.allowedOriginPatterns("*")
        } else {
            mapping.allowedOrigins(*allowedOrigins.split(",").toTypedArray())
                .allowCredentials(true)
        }
    }
}
