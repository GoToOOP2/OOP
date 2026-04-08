package com.jaeyong.oop.presentation.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaeyong.oop.application.result.TokenResult
import com.jaeyong.oop.application.usecase.AuthUseCase
import com.jaeyong.oop.presentation.auth.interceptor.CurrentMemberArgumentResolver
import com.jaeyong.oop.presentation.auth.interceptor.JwtAuthenticationInterceptor
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.presentation.api.dto.LoginRequest
import com.jaeyong.oop.presentation.api.dto.ReissueRequest
import com.jaeyong.oop.presentation.api.dto.SignupRequest
import com.jaeyong.oop.presentation.exception.GlobalExceptionHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.mockito.BDDMockito.willThrow
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class AuthControllerTest {
    private lateinit var mockMvc: MockMvc
    private val objectMapper = ObjectMapper()

    @Mock
    private lateinit var authUseCase: AuthUseCase

    private val currentMemberArgumentResolver = CurrentMemberArgumentResolver()

    @BeforeEach
    fun setUp() {
        mockMvc =
            MockMvcBuilders.standaloneSetup(AuthController(authUseCase))
                .setControllerAdvice(GlobalExceptionHandler())
                .setCustomArgumentResolvers(currentMemberArgumentResolver)
                .build()
    }

    private fun <T> anyNonNull(): T {
        ArgumentMatchers.any<T>()
        @Suppress("UNCHECKED_CAST")
        return null as T
    }

    @Test
    @DisplayName("POST /api/v1/auth/signup 성공 시 201을 반환해야 한다")
    fun `회원가입 성공 시 201을 반환한다`() {
        // given
        willDoNothing().given(authUseCase).signup(anyNonNull())
        val request = SignupRequest("test@example.com", "password1", "닉네임")

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
    }

    @Test
    @DisplayName("POST /api/v1/auth/signup 이메일 형식이 잘못되면 400을 반환해야 한다")
    fun `이메일 형식이 잘못되면 400을 반환한다`() {
        // given
        val request = SignupRequest("invalid-email", "password1", "닉네임")

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("POST /api/v1/auth/signup 비밀번호가 8자 미만이면 400을 반환해야 한다")
    fun `비밀번호가 8자 미만이면 400을 반환한다`() {
        // given
        val request = SignupRequest("test@example.com", "pass1", "닉네임")

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("POST /api/v1/auth/signup 중복 이메일이면 400을 반환해야 한다")
    fun `중복 이메일이면 400을 반환한다`() {
        // given
        willThrow(BaseException(ErrorCode.DUPLICATE_EMAIL)).given(authUseCase).signup(anyNonNull())
        val request = SignupRequest("test@example.com", "password1", "닉네임")

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("A006"))
    }

    @Test
    @DisplayName("POST /api/v1/auth/login 성공 시 토큰을 반환해야 한다")
    fun `로그인 성공 시 토큰을 반환한다`() {
        // given
        val tokenResult = TokenResult("access-token", "refresh-token")
        given(authUseCase.login(anyNonNull())).willReturn(tokenResult)
        val request = LoginRequest("test@example.com", "password1")

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.accessToken").value("access-token"))
            .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
    }

    @Test
    @DisplayName("POST /api/v1/auth/login 실패 시 400을 반환해야 한다")
    fun `로그인 실패 시 400을 반환한다`() {
        // given
        given(authUseCase.login(anyNonNull())).willThrow(BaseException(ErrorCode.LOGIN_FAILED))
        val request = LoginRequest("test@example.com", "wrongPassword")

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("A003"))
    }

    @Test
    @DisplayName("POST /api/v1/auth/reissue 성공 시 새 토큰을 반환해야 한다")
    fun `토큰 재발급 성공 시 새 토큰을 반환한다`() {
        // given
        val tokenResult = TokenResult("new-access", "new-refresh")
        given(authUseCase.reissue("valid-refresh-token")).willReturn(tokenResult)
        val request = ReissueRequest("valid-refresh-token")

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.accessToken").value("new-access"))
            .andExpect(jsonPath("$.data.refreshToken").value("new-refresh"))
    }

    // ── 유효성 검증 추가 케이스 ──

    @Test
    @DisplayName("POST /api/v1/auth/signup 닉네임이 2자 미만이면 400을 반환해야 한다")
    fun `닉네임이 2자 미만이면 400을 반환한다`() {
        // given
        val request = SignupRequest("test@example.com", "password1", "A")

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("POST /api/v1/auth/signup 비밀번호에 영문이 없으면 400을 반환해야 한다")
    fun `비밀번호에 영문이 없으면 400을 반환한다`() {
        // given
        val request = SignupRequest("test@example.com", "12345678", "닉네임")

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("POST /api/v1/auth/login 이메일 형식이 잘못되면 400을 반환해야 한다")
    fun `로그인 이메일 형식이 잘못되면 400을 반환한다`() {
        // given
        val request = LoginRequest("invalid-email", "password1")

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("POST /api/v1/auth/reissue refreshToken이 비어있으면 400을 반환해야 한다")
    fun `refreshToken이 비어있으면 400을 반환한다`() {
        // given
        val request = ReissueRequest("")

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    // ── 로그아웃 테스트 ──

    @Test
    @DisplayName("POST /api/v1/auth/logout 성공 시 200을 반환해야 한다")
    fun `로그아웃 성공 시 200을 반환한다`() {
        // given
        willDoNothing().given(authUseCase).logout(1L)

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/logout")
                .requestAttr(JwtAuthenticationInterceptor.MEMBER_ID_ATTRIBUTE, 1L),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
    }
}
