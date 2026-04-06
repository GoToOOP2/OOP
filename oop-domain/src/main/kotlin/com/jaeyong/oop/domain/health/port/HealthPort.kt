package com.jaeyong.oop.domain.health.port

import com.jaeyong.oop.domain.health.Health

/**
 * Outbound Port (도메인 레이어에서 정의한 저장 인터페이스)
 */
interface HealthPort {
    fun save(health: Health): Health
}
