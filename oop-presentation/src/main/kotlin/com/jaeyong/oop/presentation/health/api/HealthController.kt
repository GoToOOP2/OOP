package com.jaeyong.oop.presentation.health.api

import com.jaeyong.oop.application.health.usecase.HealthCheckUseCase
import com.jaeyong.oop.presentation.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/health")
class HealthController(
    private val healthCheckUseCase: HealthCheckUseCase
) {
    @GetMapping
    fun healthCheck(): ResponseEntity<ApiResponse<String>> {
        val result = healthCheckUseCase.checkHealth()
        return ApiResponse.success(result)
    }
}
