package com.jaeyong.oop.infrastructure.persistence

import com.jaeyong.oop.domain.Health
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
    val status: String,
    val createdAt: LocalDateTime
) {
    // 도메인 모델 <-> DB 엔티티 변환 메서드
    fun toDomain(): Health = Health(id, status, createdAt)
    
    companion object {
        fun fromDomain(health: Health): HealthEntity = 
            HealthEntity(health.id, health.status, health.createdAt)
    }
}
