package com.jaeyong.oop.infrastructure.jwt

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class JwtProviderImplTest {

    private lateinit var sut: JwtProviderImpl

    @BeforeEach
    fun setUp() {
        sut = JwtProviderImpl(
            secret = "test-secret-key-must-be-at-least-256-bits-long-here-padding",
            expiration = 86400000L
        )
    }

    @Test
    @DisplayName("1. 발급한 토큰에서 username을 추출할 수 있다")
    fun `발급한 토큰에서 username을 추출할 수 있다`() {
        // given
        val token = sut.generateToken("jaeyong")

        // when
        val username = sut.extractUsername(token)

        // then
        assertThat(username).isEqualTo("jaeyong")
    }

    @Test
    @DisplayName("2. 발급한 토큰은 유효하다")
    fun `발급한 토큰은 유효하다`() {
        // given
        val token = sut.generateToken("jaeyong")

        // when & then
        assertThat(sut.isValid(token)).isTrue()
    }

    @Test
    @DisplayName("3. 형식이 잘못된 토큰은 유효하지 않다")
    fun `형식이 잘못된 토큰은 유효하지 않다`() {
        // when & then
        assertThat(sut.isValid("invalid.token.string")).isFalse()
    }

    @Test
    @DisplayName("4. 다른 서명키로 위변조된 토큰은 유효하지 않다")
    fun `다른 서명키로 위변조된 토큰은 유효하지 않다`() {
        // given
        val forgedProvider = JwtProviderImpl(
            secret = "forged-secret-key-must-be-at-least-256-bits-long-here-pad",
            expiration = 86400000L
        )
        val forgedToken = forgedProvider.generateToken("jaeyong")

        // when & then
        assertThat(sut.isValid(forgedToken)).isFalse()
    }

    @Test
    @DisplayName("5. 만료된 토큰은 유효하지 않다")
    fun `만료된 토큰은 유효하지 않다`() {
        // given — expiration 0ms (즉시 만료)
        val expiredProvider = JwtProviderImpl(
            secret = "test-secret-key-must-be-at-least-256-bits-long-here-padding",
            expiration = 0L
        )
        val expiredToken = expiredProvider.generateToken("jaeyong")

        // when & then
        assertThat(sut.isValid(expiredToken)).isFalse()
    }

    @Test
    @DisplayName("6. 빈 문자열 토큰은 유효하지 않다 (IllegalArgumentException)")
    fun `빈 문자열 토큰은 유효하지 않다`() {
        // when & then
        assertThat(sut.isValid("")).isFalse()
    }
}
