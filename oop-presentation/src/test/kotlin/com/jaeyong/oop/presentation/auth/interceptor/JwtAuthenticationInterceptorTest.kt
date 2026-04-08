package com.jaeyong.oop.presentation.auth.interceptor

import com.jaeyong.oop.application.usecase.TokenValidationUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

@ExtendWith(MockitoExtension::class)
class JwtAuthenticationInterceptorTest {
    @Mock
    private lateinit var tokenValidationUseCase: TokenValidationUseCase

    @InjectMocks
    private lateinit var interceptor: JwtAuthenticationInterceptor

    private val response = MockHttpServletResponse()
    private val handler = Any()

    @Test
    @DisplayName("Authorization 헤더가 없으면 UNAUTHORIZED 예외를 던져야 한다")
    fun `Authorization 헤더가 없으면 UNAUTHORIZED 예외를 던진다`() {
        // given
        val request = MockHttpServletRequest()

        // when & then
        assertThatThrownBy { interceptor.preHandle(request, response, handler) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.UNAUTHORIZED)
    }

    @Test
    @DisplayName("Bearer 접두사가 없으면 UNAUTHORIZED 예외를 던져야 한다")
    fun `Bearer 접두사가 없으면 UNAUTHORIZED 예외를 던진다`() {
        // given
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Token some-token")

        // when & then
        assertThatThrownBy { interceptor.preHandle(request, response, handler) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.UNAUTHORIZED)
    }

    @Test
    @DisplayName("유효한 토큰이면 memberId를 request attribute에 저장해야 한다")
    fun `유효한 토큰이면 memberId를 request attribute에 저장한다`() {
        // given
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer valid-token")
        `when`(tokenValidationUseCase.extractMemberId("valid-token")).thenReturn(1L)

        // when
        val result = interceptor.preHandle(request, response, handler)

        // then
        assertThat(result).isTrue()
        assertThat(request.getAttribute("memberId")).isEqualTo(1L)
        verify(tokenValidationUseCase).validateToken("valid-token")
        verify(tokenValidationUseCase).extractMemberId("valid-token")
    }

    @Test
    @DisplayName("만료된 토큰이면 EXPIRED_TOKEN 예외를 던져야 한다")
    fun `만료된 토큰이면 EXPIRED_TOKEN 예외를 던진다`() {
        // given
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer expired-token")
        `when`(tokenValidationUseCase.validateToken("expired-token"))
            .thenThrow(BaseException(ErrorCode.EXPIRED_TOKEN))

        // when & then
        assertThatThrownBy { interceptor.preHandle(request, response, handler) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.EXPIRED_TOKEN)
    }

    @Test
    @DisplayName("변조된 토큰이면 INVALID_TOKEN 예외를 던져야 한다")
    fun `변조된 토큰이면 INVALID_TOKEN 예외를 던진다`() {
        // given
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer tampered-token")
        `when`(tokenValidationUseCase.validateToken("tampered-token"))
            .thenThrow(BaseException(ErrorCode.INVALID_TOKEN))

        // when & then
        assertThatThrownBy { interceptor.preHandle(request, response, handler) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.INVALID_TOKEN)
    }
}
