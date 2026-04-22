package com.jaeyong.oop.infrastructure.health.entity

import com.jaeyong.oop.domain.health.Health
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * health_check 테이블 JPA 엔티티. Infrastructure 레이어에서만 사용한다.
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
    /**
     * DB 조회 결과를 도메인 객체로 변환한다.
     *
     * @return 복원된 [Health] 도메인 객체
     */
    fun toDomain(): Health = Health.reconstruct(id, status, createdAt)

    companion object {
        /**
         * 도메인 객체를 JPA 저장용 Entity로 변환한다.
         *
         * @param health 변환할 [Health] 도메인 객체
         * @return 변환된 [HealthEntity]
         */
        fun fromDomain(health: Health): HealthEntity =
            HealthEntity(health.id, health.status, health.createdAt)
    }
}
