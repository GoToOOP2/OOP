package com.jaeyong.oop.infrastructure.persistence.token

import org.springframework.data.jpa.repository.JpaRepository

/** Refresh Token JPA Repository. RefreshTokenPersistenceAdapter에서만 사용된다. */
interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun findByMemberId(memberId: Long): RefreshTokenEntity?

    fun deleteByMemberId(memberId: Long)
}
