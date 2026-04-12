package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.common.TokenValidationCommand
import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.vo.UsernameVO
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
        given(jwtHandlerPort.validateAndExtract(TokenVO.from("valid.token"))).willReturn(UsernameVO.from("jaeyong"))

        // when
        val result = sut.validateAndExtract(TokenValidationCommand.of("valid.token"))

        // then
        assertThat(result.username).isEqualTo("jaeyong")
    }

    @Test
    @DisplayName("2. 유효하지 않은 토큰이면 null을 반환한다")
    fun `유효하지 않은 토큰이면 null을 반환한다`() {
        // given
        given(jwtHandlerPort.validateAndExtract(TokenVO.from("invalid.token"))).willReturn(null)

        // when
        val result = sut.validateAndExtract(TokenValidationCommand.of("invalid.token"))

        // then
        assertThat(result.username).isNull()
    }
}
