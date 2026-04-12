package com.jaeyong.oop.infrastructure.health.repository

import com.jaeyong.oop.infrastructure.health.entity.HealthEntity
import org.springframework.data.jpa.repository.JpaRepository

interface HealthJpaRepository : JpaRepository<HealthEntity, Long>, HealthEntityRepository
