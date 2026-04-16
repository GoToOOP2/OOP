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

/**
 * [JwtHandlerPort], [RefreshTokenHandlerPort] Adapter — JJWT를 사용해 JWT를 발급하고 검증한다.
 *
 * access/refresh token을 `type` claim으로 구분해 혼용을 방지한다.
 * userId를 claim에 포함하여 토큰에서 직접 추출할 수 있도록 한다.
 */
@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtHandlerAdapter(
    private val jwtProperties: JwtProperties
) : JwtHandlerPort, RefreshTokenHandlerPort {

    private val secretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(Charsets.UTF_8))
    }

    override fun generateToken(username: UsernameVO, userId: Long): TokenVO =
        TokenVO.from(
            Jwts.builder()
                .subject(username.value)
                .claim("type", "access")
                .claim("userId", userId)
                .issuedAt(Date())
                .expiration(Date(System.currentTimeMillis() + jwtProperties.expiration))
                .signWith(secretKey)
                .compact()
        )

    override fun validateAndExtract(token: TokenVO): Long? =
        extractUserIdIfType(token, "access")

    override fun generateRefreshToken(username: UsernameVO, userId: Long): TokenVO =
        TokenVO.from(
            Jwts.builder()
                .subject(username.value)
                .claim("type", "refresh")
                .claim("userId", userId)
                .issuedAt(Date())
                .expiration(Date(System.currentTimeMillis() + jwtProperties.refreshExpiration))
                .signWith(secretKey)
                .compact()
        )

    override fun validateAndExtractRefresh(token: TokenVO): Long? =
        extractUserIdIfType(token, "refresh")

    private fun extractUserIdIfType(token: TokenVO, expectedType: String): Long? =
        try {
            val claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token.value)
                .payload
            if (claims["type"] == expectedType) {
                claims["userId"]?.let { (it as Number).toLong() }
            } else {
                null
            }
        } catch (e: JwtException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
}
