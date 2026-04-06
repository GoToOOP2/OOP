package com.jaeyong.oop.presentation.api

import com.jaeyong.oop.application.service.HealthService
import com.jaeyong.oop.presentation.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/health")
class HealthController(
    private val healthService: HealthService
) {
    @GetMapping
    fun healthCheck(): ResponseEntity<ApiResponse<String>> {
        val result = healthService.checkHealth()
        return ApiResponse.success(result)
    }
}
