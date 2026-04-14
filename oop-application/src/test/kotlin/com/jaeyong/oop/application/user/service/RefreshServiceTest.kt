package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.common.RefreshCommand
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.vo.UsernameVO
import com.jaeyong.oop.domain.user.port.JwtHandlerPort
import com.jaeyong.oop.domain.user.port.RefreshTokenHandlerPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class RefreshServiceTest {

    @Mock
    private lateinit var jwtHandlerPort: JwtHandlerPort

    @Mock
    private lateinit var refreshTokenHandlerPort: RefreshTokenHandlerPort

    @InjectMocks
    private lateinit var sut: RefreshService

    @Test
    @DisplayName("1. 유효한 refresh token으로 access token과 refresh token을 재발급한다")
    fun `유효한 refresh token으로 access token과 refresh token을 재발급한다`() {
        given(refreshTokenHandlerPort.validateAndExtractRefresh(TokenVO.from("valid.refresh.token")))
            .willReturn(UsernameVO.from("jaeyong"))
        given(jwtHandlerPort.generateToken(UsernameVO.from("jaeyong")))
            .willReturn(TokenVO.from("new.access.token"))
        given(refreshTokenHandlerPort.generateRefreshToken(UsernameVO.from("jaeyong")))
            .willReturn(TokenVO.from("new.refresh.token"))

        val result = sut.refresh(RefreshCommand.of("valid.refresh.token"))

        assertThat(result.accessToken).isEqualTo("new.access.token")
        assertThat(result.refreshToken).isEqualTo("new.refresh.token")
    }

    @Test
    @DisplayName("2. 유효하지 않은 refresh token이면 UNAUTHORIZED 예외를 던진다")
    fun `유효하지 않은 refresh token이면 UNAUTHORIZED 예외를 던진다`() {
        given(refreshTokenHandlerPort.validateAndExtractRefresh(TokenVO.from("invalid.token")))
            .willReturn(null)

        val exception = assertThrows<BaseException> {
            sut.refresh(RefreshCommand.of("invalid.token"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.UNAUTHORIZED)
    }

    @Test
    @DisplayName("3. access token을 refresh 엔드포인트에 사용하면 UNAUTHORIZED 예외를 던진다")
    fun `access token을 refresh 엔드포인트에 사용하면 UNAUTHORIZED 예외를 던진다`() {
        given(refreshTokenHandlerPort.validateAndExtractRefresh(TokenVO.from("access.token.string")))
            .willReturn(null)

        val exception = assertThrows<BaseException> {
            sut.refresh(RefreshCommand.of("access.token.string"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.UNAUTHORIZED)
    }
}
