package com.jaeyong.oop.infrastructure.security

import com.jaeyong.oop.domain.port.JwtPort
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

/**
 * JwtPort의 구현체 (아웃바운드 어댑터).
 * JJWT 라이브러리를 사용하여 JWT 토큰을 생성, 검증, 파싱한다.
 *
 * 설정값은 application.yml에서 주입:
 * - jwt.secret: 서명에 사용하는 비밀키 (HS256, 최소 256비트)
 * - jwt.access-token-expiry: Access Token 유효기간 (밀리초, 기본 30분 = 1800000)
 * - jwt.refresh-token-expiry: Refresh Token 유효기간 (밀리초, 기본 14일 = 1209600000)
 *
 * 토큰 payload에는 subject(memberId)만 포함 — 이메일 등 개인정보를 넣지 않는다.
 */
@Component
class JwtAdapter(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.access-token-expiry}") private val accessTokenExpiry: Long,
    @Value("\${jwt.refresh-token-expiry}") private val refreshTokenExpiry: Long,
) : JwtPort {

    // lazy 초기화: 첫 사용 시점에 secret 문자열 → HMAC-SHA 키 객체로 변환
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    override fun createAccessToken(memberId: Long): String {
        return createToken(memberId, accessTokenExpiry)
    }

    override fun createRefreshToken(memberId: Long): String {
        return createToken(memberId, refreshTokenExpiry)
    }

    /** 토큰의 subject 클레임에서 memberId를 추출한다. */
    override fun extractMemberId(token: String): Long {
        val claims =
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        return claims.subject.toLong()
    }

    /**
     * 토큰 유효성 검증.
     * - 만료된 토큰 → EXPIRED_TOKEN 예외 (클라이언트가 reissue 요청해야 함)
     * - 변조/잘못된 형식 → INVALID_TOKEN 예외
     */
    override fun validateToken(token: String) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
        } catch (e: ExpiredJwtException) {
            throw BaseException(ErrorCode.EXPIRED_TOKEN)
        } catch (e: Exception) {
            throw BaseException(ErrorCode.INVALID_TOKEN)
        }
    }

    /** JWT 토큰 생성 공통 로직. subject에 memberId, 발행시간, 만료시간을 설정하고 서명한다. */
    private fun createToken(memberId: Long, expiry: Long, ): String {
        val now = Date()
        return Jwts.builder()
            .subject(memberId.toString()) // payload에 memberId만 포함 (개인정보 없음)
            .issuedAt(now)
            .expiration(Date(now.time + expiry))
            .signWith(key) // HS256 서명
            .compact()
    }
}
