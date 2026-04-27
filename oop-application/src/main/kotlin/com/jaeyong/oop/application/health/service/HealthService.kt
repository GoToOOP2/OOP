package com.jaeyong.oop.application.health.service

import com.jaeyong.oop.application.health.result.HealthCheckResult
import com.jaeyong.oop.application.health.usecase.HealthCheckUseCase
import com.jaeyong.oop.domain.health.Health
import com.jaeyong.oop.domain.health.port.HealthPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HealthService(
    private val healthRepository: HealthPort
) : HealthCheckUseCase {
    @Transactional
    override fun checkHealth(): HealthCheckResult {
        val health = Health.ok()
        healthRepository.save(health)
        return HealthCheckResult.from("success")
    }
}
