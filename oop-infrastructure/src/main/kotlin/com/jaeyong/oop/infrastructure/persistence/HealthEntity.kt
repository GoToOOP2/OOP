package com.jaeyong.oop.infrastructure.persistence

import com.jaeyong.oop.domain.health.Health
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
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
    val createdAt: LocalDateTime,
) {
    // 도메인 모델 <-> DB 엔티티 변환 메서드
    fun toDomain(): Health = Health(id, status, createdAt)

    companion object {
        fun fromDomain(health: Health): HealthEntity = HealthEntity(health.id, health.status, health.createdAt)
    }
}
