package com.jaeyong.oop.infrastructure.health.entity

import com.jaeyong.oop.domain.health.Health
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * DB 엔티티 (기술 레이어인 Infrastructure에서만 사용)
 */
@Entity
@Table(name = "health_check")
class HealthEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "status", nullable = false, length = 50)
    val status: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime
) {
    fun toDomain(): Health = Health(id, status, createdAt)

    companion object {
        fun fromDomain(health: Health): HealthEntity =
            HealthEntity(health.id, health.status, health.createdAt)
    }
}
