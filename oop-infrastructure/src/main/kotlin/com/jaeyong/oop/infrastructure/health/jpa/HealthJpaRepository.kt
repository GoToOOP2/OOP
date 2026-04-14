package com.jaeyong.oop.infrastructure.health.jpa

import com.jaeyong.oop.infrastructure.health.entity.HealthEntity
import com.jaeyong.oop.infrastructure.health.repository.HealthEntityRepository
import org.springframework.data.jpa.repository.JpaRepository

interface HealthJpaRepository : JpaRepository<HealthEntity, Long>, HealthEntityRepository
