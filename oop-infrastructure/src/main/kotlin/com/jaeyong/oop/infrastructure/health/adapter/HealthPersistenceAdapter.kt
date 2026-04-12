package com.jaeyong.oop.infrastructure.health.adapter

import com.jaeyong.oop.domain.health.Health
import com.jaeyong.oop.domain.health.port.HealthPort
import com.jaeyong.oop.infrastructure.health.entity.HealthEntity
import com.jaeyong.oop.infrastructure.health.repository.HealthEntityRepository
import org.springframework.stereotype.Repository

/**
 * Outbound Adapter (도메인 인터페이스를 실제 기술로 구현)
 */
@Repository
class HealthPersistenceAdapter(
    private val healthEntityRepository: HealthEntityRepository
) : HealthPort {
    override fun save(health: Health): Health {
        val entity = HealthEntity.fromDomain(health)
        return healthEntityRepository.save(entity).toDomain()
    }
}
