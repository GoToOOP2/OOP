package com.jaeyong.oop.presentation.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaeyong.oop.application.user.usecase.JoinUseCase
import com.jaeyong.oop.application.user.usecase.LoginUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.presentation.exception.GlobalExceptionHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
class UserControllerTest {

    private lateinit var mockMvc: MockMvc
    private val objectMapper = ObjectMapper()

    @Mock
    private lateinit var joinUseCase: JoinUseCase

    @Mock
    private lateinit var loginUseCase: LoginUseCase

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(UserController(joinUseCase, loginUseCase))
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    @DisplayName("1. 정상 회원가입 시 201을 반환한다")
    fun `정상 회원가입 시 201을 반환한다`() {
        // given
        willDoNothing().given(joinUseCase).join("jaeyong", "password123")
        val body = mapOf("username" to "jaeyong", "password" to "password123")

        // when & then
        mockMvc.perform(
            post("/api/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
    }

    @Test
    @DisplayName("2. 중복 username으로 가입 시 400과 D002를 반환한다")
    fun `중복 username으로 가입 시 400과 D002를 반환한다`() {
        // given
        willThrow(BaseException(ErrorCode.DUPLICATE)).given(joinUseCase).join("jaeyong", "password123")
        val body = mapOf("username" to "jaeyong", "password" to "password123")

        // when & then
        mockMvc.perform(
            post("/api/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("D002"))
    }

    @Test
    @DisplayName("3. 정상 로그인 시 200과 accessToken을 반환한다")
    fun `정상 로그인 시 200과 accessToken을 반환한다`() {
        // given
        given(loginUseCase.login("jaeyong", "password123")).willReturn("jwt.token.string")
        val body = mapOf("username" to "jaeyong", "password" to "password123")

        // when & then
        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.accessToken").value("jwt.token.string"))
    }

    @Test
    @DisplayName("4. 잘못된 자격증명으로 로그인 시 400과 A001을 반환한다")
    fun `잘못된 자격증명으로 로그인 시 400과 A001을 반환한다`() {
        // given
        willThrow(BaseException(ErrorCode.UNAUTHORIZED)).given(loginUseCase).login("jaeyong", "wrongpassword")
        val body = mapOf("username" to "jaeyong", "password" to "wrongpassword")

        // when & then
        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("A001"))
    }
}
