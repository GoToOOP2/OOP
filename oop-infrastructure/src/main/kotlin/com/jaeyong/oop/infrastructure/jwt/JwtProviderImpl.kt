package com.jaeyong.oop.infrastructure.jwt

import com.jaeyong.oop.domain.user.port.JwtProvider
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtProviderImpl(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
) : JwtProvider {

    private val secretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))
    }

    override fun generateToken(username: String): String =
        Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(secretKey)
            .compact()

    override fun extractUsername(token: String): String =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject

    override fun isValid(token: String): Boolean =
        runCatching {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
            true
        }.getOrElse { e ->
            if (e is JwtException || e is IllegalArgumentException) false
            else throw e
        }
}
