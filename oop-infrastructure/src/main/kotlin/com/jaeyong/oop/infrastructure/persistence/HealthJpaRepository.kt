package com.jaeyong.oop.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface HealthJpaRepository : JpaRepository<HealthEntity, Long>
