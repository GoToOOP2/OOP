package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.domain.user.port.JwtHandlerPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class TokenValidationServiceTest {

    @Mock
    private lateinit var jwtHandlerPort: JwtHandlerPort

    @InjectMocks
    private lateinit var sut: TokenValidationService

    @Test
    @DisplayName("1. 유효한 토큰이면 username을 반환한다")
    fun `유효한 토큰이면 username을 반환한다`() {
        // given
        given(jwtHandlerPort.validateAndExtract("valid.token")).willReturn("jaeyong")

        // when
        val result = sut.validateAndExtract("valid.token")

        // then
        assertThat(result).isEqualTo("jaeyong")
    }

    @Test
    @DisplayName("2. 유효하지 않은 토큰이면 null을 반환한다")
    fun `유효하지 않은 토큰이면 null을 반환한다`() {
        // given
        given(jwtHandlerPort.validateAndExtract("invalid.token")).willReturn(null)

        // when
        val result = sut.validateAndExtract("invalid.token")

        // then
        assertThat(result).isNull()
    }
}
