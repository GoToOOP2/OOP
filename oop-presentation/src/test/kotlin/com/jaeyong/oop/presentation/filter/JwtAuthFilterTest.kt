package com.jaeyong.oop.presentation.filter

import com.jaeyong.oop.application.user.usecase.TokenValidationUseCase
import com.jaeyong.oop.common.auth.AuthContext
import jakarta.servlet.FilterChain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
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

    private lateinit var sut: JwtAuthFilter

    @BeforeEach
    fun setUp() {
        sut = JwtAuthFilter(tokenValidationUseCase)
    }

    @AfterEach
    fun tearDown() {
        AuthContext.clear()
    }

    @Test
    @DisplayName("1. 유효한 토큰이 있으면 filterChain이 호출된다")
    fun `유효한 토큰이 있으면 filterChain이 호출된다`() {
        // given
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer valid.token")
        }
        val response = MockHttpServletResponse()
        given(tokenValidationUseCase.validateAndExtract("valid.token")).willReturn("jaeyong")

        // when
        sut.doFilter(request, response, filterChain)

        // then
        org.mockito.Mockito.verify(filterChain).doFilter(request, response)
    }

    @Test
    @DisplayName("2. Authorization 헤더가 없으면 AuthContext가 비어있다")
    fun `Authorization 헤더가 없으면 AuthContext가 비어있다`() {
        // given
        val request = MockHttpServletRequest() // 헤더 없음
        val response = MockHttpServletResponse()

        // when
        sut.doFilter(request, response, filterChain)

        // then
        assertThat(AuthContext.get()).isNull()
    }

    @Test
    @DisplayName("3. 유효하지 않은 토큰이면 AuthContext가 비어있다")
    fun `유효하지 않은 토큰이면 AuthContext가 비어있다`() {
        // given
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer invalid.token")
        }
        given(tokenValidationUseCase.validateAndExtract("invalid.token")).willReturn(null)

        // when
        sut.doFilter(request, MockHttpServletResponse(), filterChain)

        // then
        assertThat(AuthContext.get()).isNull()
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
        assertThat(AuthContext.get()).isNull()
    }

    @Test
    @DisplayName("5. 요청 처리 후 AuthContext가 초기화된다")
    fun `요청 처리 후 AuthContext가 초기화된다`() {
        // given
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer valid.token")
        }
        given(tokenValidationUseCase.validateAndExtract("valid.token")).willReturn("jaeyong")

        // when
        sut.doFilter(request, MockHttpServletResponse(), filterChain)

        // then — 필터 종료 후 clear() 호출로 비워져야 함
        assertThat(AuthContext.get()).isNull()
    }
}
