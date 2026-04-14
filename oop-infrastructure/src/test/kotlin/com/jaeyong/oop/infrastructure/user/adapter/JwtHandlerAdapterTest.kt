package com.jaeyong.oop.infrastructure.user.adapter

import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.vo.UsernameVO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class JwtHandlerAdapterTest {

    private lateinit var sut: JwtHandlerAdapter

    @BeforeEach
    fun setUp() {
        sut = JwtHandlerAdapter(
            JwtProperties(
                secret = "test-secret-key-must-be-at-least-256-bits-long-here-padding",
                expiration = 86400000L,
                refreshExpiration = 604800000L
            )
        )
    }

    @Test
    @DisplayName("1. 발급한 access 토큰에서 username을 추출할 수 있다")
    fun `발급한 access 토큰에서 username을 추출할 수 있다`() {
        val token = sut.generateToken(UsernameVO.from("jaeyong"))
        val username = sut.validateAndExtract(token)
        assertThat(username).isEqualTo(UsernameVO.from("jaeyong"))
    }

    @Test
    @DisplayName("2. 형식이 잘못된 토큰은 null을 반환한다")
    fun `형식이 잘못된 토큰은 null을 반환한다`() {
        assertThat(sut.validateAndExtract(TokenVO.from("invalid.token.string"))).isNull()
    }

    @Test
    @DisplayName("3. 다른 서명키로 위변조된 토큰은 null을 반환한다")
    fun `다른 서명키로 위변조된 토큰은 null을 반환한다`() {
        val forgedProvider = JwtHandlerAdapter(
            JwtProperties(
                secret = "forged-secret-key-must-be-at-least-256-bits-long-here-pad",
                expiration = 86400000L,
                refreshExpiration = 604800000L
            )
        )
        val forgedToken = forgedProvider.generateToken(UsernameVO.from("jaeyong"))
        assertThat(sut.validateAndExtract(forgedToken)).isNull()
    }

    @Test
    @DisplayName("4. 만료된 토큰은 null을 반환한다")
    fun `만료된 토큰은 null을 반환한다`() {
        val expiredProvider = JwtHandlerAdapter(
            JwtProperties(
                secret = "test-secret-key-must-be-at-least-256-bits-long-here-padding",
                expiration = 0L,
                refreshExpiration = 604800000L
            )
        )
        val expiredToken = expiredProvider.generateToken(UsernameVO.from("jaeyong"))
        assertThat(sut.validateAndExtract(expiredToken)).isNull()
    }

    @Test
    @DisplayName("5. 빈 문자열 토큰은 null을 반환한다")
    fun `빈 문자열 토큰은 null을 반환한다`() {
        assertThat(sut.validateAndExtract(TokenVO.from(""))).isNull()
    }

    @Test
    @DisplayName("6. refresh 토큰을 validateAndExtract에 사용하면 null을 반환한다")
    fun `refresh 토큰을 validateAndExtract에 사용하면 null을 반환한다`() {
        val refreshToken = sut.generateRefreshToken(UsernameVO.from("jaeyong"))
        assertThat(sut.validateAndExtract(refreshToken)).isNull()
    }

    @Test
    @DisplayName("7. 발급한 refresh 토큰에서 username을 추출할 수 있다")
    fun `발급한 refresh 토큰에서 username을 추출할 수 있다`() {
        val refreshToken = sut.generateRefreshToken(UsernameVO.from("jaeyong"))
        val username = sut.validateAndExtractRefresh(refreshToken)
        assertThat(username).isEqualTo(UsernameVO.from("jaeyong"))
    }

    @Test
    @DisplayName("8. access 토큰을 validateAndExtractRefresh에 사용하면 null을 반환한다")
    fun `access 토큰을 validateAndExtractRefresh에 사용하면 null을 반환한다`() {
        val accessToken = sut.generateToken(UsernameVO.from("jaeyong"))
        assertThat(sut.validateAndExtractRefresh(accessToken)).isNull()
    }

    @Test
    @DisplayName("9. 만료된 refresh 토큰은 null을 반환한다")
    fun `만료된 refresh 토큰은 null을 반환한다`() {
        val expiredProvider = JwtHandlerAdapter(
            JwtProperties(
                secret = "test-secret-key-must-be-at-least-256-bits-long-here-padding",
                expiration = 86400000L,
                refreshExpiration = 0L
            )
        )
        val expiredToken = expiredProvider.generateRefreshToken(UsernameVO.from("jaeyong"))
        assertThat(sut.validateAndExtractRefresh(expiredToken)).isNull()
    }
}
