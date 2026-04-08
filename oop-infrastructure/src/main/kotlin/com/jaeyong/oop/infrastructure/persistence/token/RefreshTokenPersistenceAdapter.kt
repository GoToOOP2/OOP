package com.jaeyong.oop.infrastructure.persistence.token

import com.jaeyong.oop.domain.port.RefreshTokenPort
import org.springframework.stereotype.Repository

/**
 * RefreshTokenPort의 구현체 (아웃바운드 어댑터).
 * Refresh Token의 CRUD를 JPA를 통해 처리한다.
 */
@Repository
class RefreshTokenPersistenceAdapter(private val refreshTokenJpaRepository: RefreshTokenJpaRepository, ) : RefreshTokenPort {
    /**
     * Refresh Token 저장 (Upsert 방식).
     * 해당 회원의 토큰이 이미 있으면 갱신, 없으면 새로 생성한다.
     * 이렇게 하면 회원당 항상 1개의 Refresh Token만 유지된다.
     */
    override fun save(memberId: Long, hashedToken: String, ) {
        val existing = refreshTokenJpaRepository.findByMemberId(memberId)
        if (existing != null) {
            existing.updateHashedToken(hashedToken) // 더티 체킹으로 UPDATE 쿼리 발생 (리프레시 토큰 업데이트)
        } else {
            refreshTokenJpaRepository.save(RefreshTokenEntity(memberId = memberId, hashedToken = hashedToken))
        }
    }

    override fun findByMemberId(memberId: Long): String? {
        return refreshTokenJpaRepository.findByMemberId(memberId)?.hashedToken
    }

    override fun deleteByMemberId(memberId: Long) {
        refreshTokenJpaRepository.deleteByMemberId(memberId)
    }
}
