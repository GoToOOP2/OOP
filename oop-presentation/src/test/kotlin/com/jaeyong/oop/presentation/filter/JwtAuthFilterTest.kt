package com.jaeyong.oop.presentation.filter

import com.jaeyong.oop.application.user.common.TokenValidationCommand
import com.jaeyong.oop.application.user.result.TokenValidationResult
import com.jaeyong.oop.application.user.usecase.TokenValidationUseCase
import jakarta.servlet.FilterChain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

@ExtendWith(MockitoExtension::class)
class JwtAuthFilterTest {

    @Mock
    private lateinit var tokenValidationUseCase: TokenValidationUseCase

    @Mock
    private lateinit var filterChain: FilterChain

    @InjectMocks
    private lateinit var sut: JwtAuthFilter

    @Test
    @DisplayName("1. 유효한 토큰이 있으면 filterChain이 호출된다")
    fun `유효한 토큰이 있으면 filterChain이 호출된다`() {
        // given
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer valid.token")
        }
        val response = MockHttpServletResponse()
        given(tokenValidationUseCase.validateAndExtract(TokenValidationCommand.of("valid.token"))).willReturn(TokenValidationResult.of("jaeyong"))

        // when
        sut.doFilter(request, response, filterChain)

        // then
        org.mockito.Mockito.verify(filterChain).doFilter(request, response)
    }

    @Test
    @DisplayName("2. Authorization 헤더가 없으면 request attribute가 비어있다")
    fun `Authorization 헤더가 없으면 request attribute가 비어있다`() {
        // given
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        // when
        sut.doFilter(request, response, filterChain)

        // then
        assertThat(request.getAttribute("username")).isNull()
    }

    @Test
    @DisplayName("3. 유효하지 않은 토큰이면 request attribute가 비어있다")
    fun `유효하지 않은 토큰이면 request attribute가 비어있다`() {
        // given
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer invalid.token")
        }
        given(tokenValidationUseCase.validateAndExtract(TokenValidationCommand.of("invalid.token"))).willReturn(TokenValidationResult.of(null))

        // when
        sut.doFilter(request, MockHttpServletResponse(), filterChain)

        // then
        assertThat(request.getAttribute("username")).isNull()
    }

    @Test
    @DisplayName("4. Bearer 접두사 없는 Authorization 헤더는 토큰으로 인식하지 않는다")
    fun `Bearer 접두사 없는 Authorization 헤더는 토큰으로 인식하지 않는다`() {
        // given
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Basic sometoken")
        }

        // when
        sut.doFilter(request, MockHttpServletResponse(), filterChain)

        // then
        assertThat(request.getAttribute("username")).isNull()
    }

    @Test
    @DisplayName("5. 유효한 토큰이면 request attribute에 username이 저장된다")
    fun `유효한 토큰이면 request attribute에 username이 저장된다`() {
        // given
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer valid.token")
        }
        given(tokenValidationUseCase.validateAndExtract(TokenValidationCommand.of("valid.token"))).willReturn(TokenValidationResult.of("jaeyong"))

        // when
        sut.doFilter(request, MockHttpServletResponse(), filterChain)

        // then
        assertThat(request.getAttribute("username")).isEqualTo("jaeyong")
    }
}
