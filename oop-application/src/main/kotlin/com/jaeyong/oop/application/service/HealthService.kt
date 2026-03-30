package com.jaeyong.oop.application.service

import com.jaeyong.oop.domain.Health
import com.jaeyong.oop.domain.HealthRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HealthService(
    private val healthRepository: HealthRepository // 도메인에 있는 인터페이스를 주입받음
) {
    @Transactional
    fun checkHealth(): String {
        val health = Health(status = "OK")
        healthRepository.save(health)
        return "success"
    }
}
