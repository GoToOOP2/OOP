package com.jaeyong.oop.infrastructure.user.adapter

import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.vo.UsernameVO
import com.jaeyong.oop.domain.user.port.JwtHandlerPort
import com.jaeyong.oop.domain.user.port.RefreshTokenHandlerPort
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.Date

@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtHandlerAdapter(
    private val jwtProperties: JwtProperties
) : JwtHandlerPort, RefreshTokenHandlerPort {

    // 서명 키를 최초 사용 시점에 한 번만 생성. secret 문자열을 HMAC-SHA 알고리즘용 키 객체로 변환
    private val secretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(Charsets.UTF_8))
    }

    // access token 발급 — subject(username), type="access" claim, 만료 시간(30분) 포함하여 서명된 JWT 생성
    override fun generateToken(username: UsernameVO): TokenVO =
        TokenVO.from(
            Jwts.builder()
                .subject(username.value)
                .claim("type", "access")
                .issuedAt(Date())
                .expiration(Date(System.currentTimeMillis() + jwtProperties.expiration))
                .signWith(secretKey)
                .compact()
        )

    // access token 검증 — type이 "access"인 토큰에서만 username 추출. 실패 시 null 반환
    override fun validateAndExtract(token: TokenVO): UsernameVO? =
        extractSubjectIfType(token, "access")

    // refresh token 발급 — type="refresh" claim, 만료 시간(7일) 포함하여 서명된 JWT 생성
    override fun generateRefreshToken(username: UsernameVO): TokenVO =
        TokenVO.from(
            Jwts.builder()
                .subject(username.value)
                .claim("type", "refresh")
                .issuedAt(Date())
                .expiration(Date(System.currentTimeMillis() + jwtProperties.refreshExpiration))
                .signWith(secretKey)
                .compact()
        )

    // refresh token 검증 — type이 "refresh"인 토큰에서만 username 추출. 실패 시 null 반환
    override fun validateAndExtractRefresh(token: TokenVO): UsernameVO? =
        extractSubjectIfType(token, "refresh")

    // 공통 검증 로직 — 서명 검증 후 type claim이 expectedType과 일치하면 username 반환
    // 서명 불일치·만료·형식 오류는 모두 null로 처리 (예외를 호출부로 전파하지 않음)
    private fun extractSubjectIfType(token: TokenVO, expectedType: String): UsernameVO? =
        try {
            val claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token.value)
                .payload
            if (claims["type"] == expectedType) {
                claims.subject?.let { UsernameVO.from(it) }
            } else {
                null
            }
        } catch (e: JwtException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
}
