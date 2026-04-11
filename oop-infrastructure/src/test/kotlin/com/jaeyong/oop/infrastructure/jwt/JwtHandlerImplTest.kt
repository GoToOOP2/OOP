package com.jaeyong.oop.infrastructure.jwt

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class JwtHandlerImplTest {

    private lateinit var sut: JwtHandlerImpl

    @BeforeEach
    fun setUp() {
        sut = JwtHandlerImpl(
            JwtProperties(
                secret = "test-secret-key-must-be-at-least-256-bits-long-here-padding",
                expiration = 86400000L
            )
        )
    }

    @Test
    @DisplayName("1. 발급한 토큰에서 username을 추출할 수 있다")
    fun `발급한 토큰에서 username을 추출할 수 있다`() {
        // given
        val token = sut.generateToken("jaeyong")

        // when
        val username = sut.validateAndExtract(token)

        // then
        assertThat(username).isEqualTo("jaeyong")
    }

    @Test
    @DisplayName("2. 형식이 잘못된 토큰은 null을 반환한다")
    fun `형식이 잘못된 토큰은 null을 반환한다`() {
        // when & then
        assertThat(sut.validateAndExtract("invalid.token.string")).isNull()
    }

    @Test
    @DisplayName("3. 다른 서명키로 위변조된 토큰은 null을 반환한다")
    fun `다른 서명키로 위변조된 토큰은 null을 반환한다`() {
        // given
        val forgedProvider = JwtHandlerImpl(
            JwtProperties(
                secret = "forged-secret-key-must-be-at-least-256-bits-long-here-pad",
                expiration = 86400000L
            )
        )
        val forgedToken = forgedProvider.generateToken("jaeyong")

        // when & then
        assertThat(sut.validateAndExtract(forgedToken)).isNull()
    }

    @Test
    @DisplayName("4. 만료된 토큰은 null을 반환한다")
    fun `만료된 토큰은 null을 반환한다`() {
        // given
        val expiredProvider = JwtHandlerImpl(
            JwtProperties(
                secret = "test-secret-key-must-be-at-least-256-bits-long-here-padding",
                expiration = 0L
            )
        )
        val expiredToken = expiredProvider.generateToken("jaeyong")

        // when & then
        assertThat(sut.validateAndExtract(expiredToken)).isNull()
    }

    @Test
    @DisplayName("5. 빈 문자열 토큰은 null을 반환한다")
    fun `빈 문자열 토큰은 null을 반환한다`() {
        // when & then
        assertThat(sut.validateAndExtract("")).isNull()
    }
}
