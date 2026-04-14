package com.jaeyong.oop.infrastructure.health.repository

import com.jaeyong.oop.infrastructure.health.entity.HealthEntity

interface HealthEntityRepository {

    /**
     * HealthEntity를 저장하고 저장된 엔티티를 반환한다.
     *
     * @param entity 저장할 HealthEntity
     * @return 저장된 HealthEntity
     */
    fun save(entity: HealthEntity): HealthEntity
}
