package com.jaeyong.oop.infrastructure.user.adapter

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * JWT 설정 프로퍼티 (`application.yml` → `jwt.*`).
 *
 * @property secret 서명에 사용할 HMAC-SHA 비밀키
 * @property expiration access token 만료 시간 (밀리초, 기본 30분)
 * @property refreshExpiration refresh token 만료 시간 (밀리초, 기본 7일)
 */
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val expiration: Long,
    val refreshExpiration: Long
)
