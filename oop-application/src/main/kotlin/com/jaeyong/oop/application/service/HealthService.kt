package com.jaeyong.oop.application.service

import com.jaeyong.oop.application.usecase.HealthCheckUseCase
import com.jaeyong.oop.domain.health.Health
import com.jaeyong.oop.domain.health.port.HealthPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HealthService(
    private val healthPort: HealthPort
) : HealthCheckUseCase {
    @Transactional
    override fun checkHealth(): String {
        val health = Health(status = "OK")
        healthPort.save(health)
        return "success"
    }
}
