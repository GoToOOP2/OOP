package com.jaeyong.oop.domain

import java.time.LocalDateTime

/**
 * 도메인 엔티티 (순수 Kotlin 클래스)
 */
data class Health(
    val id: Long? = null,
    val status: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Outbound Port (도메인 레이어에서 정의한 저장 인터페이스)
 */
interface HealthRepository {
    fun save(health: Health): Health
}
