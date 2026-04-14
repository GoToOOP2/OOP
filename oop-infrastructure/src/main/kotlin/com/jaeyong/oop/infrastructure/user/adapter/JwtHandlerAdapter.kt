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
 */
@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtHandlerAdapter(
    private val jwtProperties: JwtProperties
) : JwtHandlerPort, RefreshTokenHandlerPort {

    /**
     * 서명 키 — 최초 사용 시점에 한 번만 생성한다.
     *
     * secret 문자열을 HMAC-SHA 알고리즘용 키 객체로 변환한다.
     */
    private val secretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(Charsets.UTF_8))
    }

    /**
     * Access token을 발급한다.
     *
     * subject(username), type="access" claim, 만료 시간(30분)을 포함한 서명된 JWT를 생성한다.
     *
     * @param username 토큰 subject로 사용할 사용자명
     * @return 서명된 access token
     */
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

    /**
     * Access token을 검증하고 사용자명을 추출한다.
     *
     * type이 "access"인 토큰에서만 username을 추출한다. 실패 시 null을 반환한다.
     *
     * @param token 검증할 access token
     * @return 유효한 access token이면 사용자명, 그 외 null
     */
    override fun validateAndExtract(token: TokenVO): UsernameVO? =
        extractSubjectIfType(token, "access")

    /**
     * Refresh token을 발급한다.
     *
     * subject(username), type="refresh" claim, 만료 시간(7일)을 포함한 서명된 JWT를 생성한다.
     *
     * @param username 토큰 subject로 사용할 사용자명
     * @return 서명된 refresh token
     */
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

    /**
     * Refresh token을 검증하고 사용자명을 추출한다.
     *
     * type이 "refresh"인 토큰에서만 username을 추출한다. 실패 시 null을 반환한다.
     *
     * @param token 검증할 refresh token
     * @return 유효한 refresh token이면 사용자명, 그 외 null
     */
    override fun validateAndExtractRefresh(token: TokenVO): UsernameVO? =
        extractSubjectIfType(token, "refresh")

    /**
     * 서명을 검증하고 type claim이 일치하면 사용자명을 반환한다.
     *
     * 서명 불일치·만료·형식 오류는 모두 null로 처리한다.
     * 예외를 호출부로 전파하지 않아 인증 실패를 일관되게 null로 표현한다.
     *
     * @param token 검증할 JWT
     * @param expectedType 기대하는 token type ("access" 또는 "refresh")
     * @return type이 일치하면 사용자명, 그 외 null
     */
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
