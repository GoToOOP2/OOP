package com.jaeyong.oop.infrastructure.health.repository

import com.jaeyong.oop.infrastructure.health.entity.HealthEntity

interface HealthEntityRepository {
    fun save(entity: HealthEntity): HealthEntity
}
