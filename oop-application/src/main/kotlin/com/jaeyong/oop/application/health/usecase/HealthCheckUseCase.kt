package com.jaeyong.oop.application.health.usecase

import com.jaeyong.oop.application.health.result.HealthCheckResult

interface HealthCheckUseCase {
    fun checkHealth(): HealthCheckResult
}
