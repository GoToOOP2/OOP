package com.jaeyong.oop.infrastructure.security

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class JwtAdapterTest {
    private val secret = "test-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm"
    private val accessTokenExpiry = 1800000L
    private val refreshTokenExpiry = 1209600000L
    private val jwtAdapter = JwtAdapter(secret, accessTokenExpiry, refreshTokenExpiry)

    @Test
    @DisplayName("Access Token을 생성하고 memberId를 추출할 수 있어야 한다")
    fun `Access Token을 생성하고 memberId를 추출할 수 있다`() {
        // given
        val memberId = 1L

        // when
        val token = jwtAdapter.createAccessToken(memberId)
        val extracted = jwtAdapter.extractMemberId(token)

        // then
        assertThat(extracted).isEqualTo(memberId)
    }

    @Test
    @DisplayName("유효한 Refresh Token은 검증을 통과해야 한다")
    fun `유효한 Refresh Token은 검증을 통과한다`() {
        // given
        val memberId = 1L
        val token = jwtAdapter.createRefreshToken(memberId)

        // when & then
        assertDoesNotThrow { jwtAdapter.validateToken(token) }
    }

    @Test
    @DisplayName("잘못된 토큰은 INVALID_TOKEN 예외를 던져야 한다")
    fun `잘못된 토큰은 INVALID_TOKEN 예외를 던진다`() {
        // when & then
        assertThatThrownBy { jwtAdapter.validateToken("invalid-token") }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.INVALID_TOKEN)
    }

    @Test
    @DisplayName("만료된 토큰은 EXPIRED_TOKEN 예외를 던져야 한다")
    fun `만료된 토큰은 EXPIRED_TOKEN 예외를 던진다`() {
        // given
        val expiredJwtAdapter = JwtAdapter(secret, 0L, 0L)
        val token = expiredJwtAdapter.createAccessToken(1L)

        // when & then
        assertThatThrownBy { jwtAdapter.validateToken(token) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.EXPIRED_TOKEN)
    }

    @Test
    @DisplayName("Refresh Token을 생성하고 memberId를 추출할 수 있어야 한다")
    fun `Refresh Token을 생성하고 memberId를 추출할 수 있다`() {
        // given
        val memberId = 1L

        // when
        val token = jwtAdapter.createRefreshToken(memberId)
        val extracted = jwtAdapter.extractMemberId(token)

        // then
        assertThat(extracted).isEqualTo(memberId)
    }

    @Test
    @DisplayName("같은 memberId로 생성해도 Access Token과 Refresh Token은 서로 달라야 한다")
    fun `같은 memberId로 생성해도 Access Token과 Refresh Token은 서로 다르다`() {
        // given
        val memberId = 1L

        // when
        val accessToken = jwtAdapter.createAccessToken(memberId)
        val refreshToken = jwtAdapter.createRefreshToken(memberId)

        // then
        assertThat(accessToken).isNotEqualTo(refreshToken)
    }

    @Test
    @DisplayName("다른 비밀키로 만든 토큰은 INVALID_TOKEN 예외를 던져야 한다")
    fun `다른 비밀키로 만든 토큰은 INVALID_TOKEN 예외를 던진다`() {
        // given
        val anotherSecret = "another-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm"
        val anotherJwtAdapter = JwtAdapter(anotherSecret, accessTokenExpiry, refreshTokenExpiry)
        val token = anotherJwtAdapter.createAccessToken(1L)

        // when & then
        assertThatThrownBy { jwtAdapter.validateToken(token) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.INVALID_TOKEN)
    }

    @Test
    @DisplayName("잘못된 토큰에서 memberId 추출 시 예외가 발생해야 한다")
    fun `잘못된 토큰에서 memberId 추출 시 예외가 발생한다`() {
        // when & then
        assertThatThrownBy { jwtAdapter.extractMemberId("invalid-token") }
            .isInstanceOf(Exception::class.java)
    }
}
