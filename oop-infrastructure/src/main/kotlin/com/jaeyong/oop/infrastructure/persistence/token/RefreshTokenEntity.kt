package com.jaeyong.oop.infrastructure.persistence.token

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * Refresh Token JPA 엔티티.
 * DB의 refresh_tokens 테이블과 매핑된다.
 *
 * 회원당 하나의 Refresh Token만 유지된다 (memberId에 unique 제약).
 * hashedToken에는 BCrypt로 해싱된 토큰이 저장된다 (평문 아님).
 * 토큰 재발급(rotation) 시 updateHashedToken()으로 기존 레코드를 갱신한다.
 */
@Entity
@Table(name = "refresh_tokens")
class RefreshTokenEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true) // 회원당 1개의 Refresh Token만 허용
    val memberId: Long,
    @Column(nullable = false) // BCrypt로 해싱된 Refresh Token
    var hashedToken: String,
) {
    /** 토큰 로테이션 시 해싱된 새 토큰으로 갱신한다. */
    fun updateHashedToken(newHashedToken: String) {
        this.hashedToken = newHashedToken
    }
}
