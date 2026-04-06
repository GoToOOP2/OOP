package com.jaeyong.oop.infrastructure.persistence

import com.jaeyong.oop.domain.health.Health
import com.jaeyong.oop.domain.health.port.HealthPort
import org.springframework.stereotype.Repository

/**
 * Outbound Adapter (도메인 인터페이스를 실제 기술로 구현)
 */
@Repository
class HealthPersistenceAdapter(
    private val healthJpaRepository: HealthJpaRepository
) : HealthPort {
    override fun save(health: Health): Health {
        val entity = HealthEntity.fromDomain(health)
        return healthJpaRepository.save(entity).toDomain()
    }
}
