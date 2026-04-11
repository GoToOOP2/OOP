package com.jaeyong.oop.infrastructure.jwt

import com.jaeyong.oop.domain.user.port.JwtHandler
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.Date

@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtHandlerImpl(
    private val jwtProperties: JwtProperties
) : JwtHandler {

    private val secretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(Charsets.UTF_8))
    }

    override fun generateToken(username: String): String =
        Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + jwtProperties.expiration))
            .signWith(secretKey)
            .compact()

    override fun validateAndExtract(token: String): String? =
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
        } catch (e: JwtException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
}
