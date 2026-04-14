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
    /**
     * 서버 상태를 확인한다.
     *
     * @return 200 OK, body: 현재 서버 상태 문자열 (예: "OK")
     */
    @GetMapping
    fun healthCheck(): ResponseEntity<ApiResponse<String>> {
        val result = healthCheckUseCase.checkHealth()
        return ApiResponse.success(result.status)
    }
}
