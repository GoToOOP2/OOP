package com.jaeyong.oop.infrastructure.user.adapter

import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.vo.UsernameVO
import com.jaeyong.oop.domain.user.port.JwtHandlerPort
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
) : JwtHandlerPort {

    // by lazy: 최초 사용 시점에 한 번만 키 생성. 매 요청마다 생성하지 않음
    private val secretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(Charsets.UTF_8))
    }

    override fun generateToken(username: UsernameVO): TokenVO =
        TokenVO.from(
            Jwts.builder()
                .subject(username.value)
                .issuedAt(Date())
                .expiration(Date(System.currentTimeMillis() + jwtProperties.expiration))
                .signWith(secretKey)
                .compact()
        )

    // 검증 실패(서명 불일치, 만료 등)는 예외 대신 null 반환
    // 호출부(Filter)에서 null이면 인증 없이 통과시키는 구조
    override fun validateAndExtract(token: TokenVO): UsernameVO? =
        try {
            val subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token.value)
                .payload
                .subject
            subject?.let { UsernameVO.from(it) }
        } catch (e: JwtException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
}
