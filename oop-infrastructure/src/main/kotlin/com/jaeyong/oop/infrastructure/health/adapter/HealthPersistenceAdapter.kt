package com.jaeyong.oop.infrastructure.health.adapter

import com.jaeyong.oop.domain.health.Health
import com.jaeyong.oop.domain.health.port.HealthPort
import com.jaeyong.oop.infrastructure.health.entity.HealthEntity
import com.jaeyong.oop.infrastructure.health.repository.HealthEntityRepository
import org.springframework.stereotype.Repository

/**
 * [HealthPort] Outbound Adapter — JPA를 통해 Health를 저장한다.
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
