package com.jaeyong.oop.application.service

import com.jaeyong.oop.domain.Health
import com.jaeyong.oop.domain.HealthOutputPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HealthService(
    private val healthRepository: HealthOutputPort
) {
    @Transactional
    fun checkHealth(): String {
        val health = Health(status = "OK")
        healthRepository.save(health)
        return "success"
    }
}
