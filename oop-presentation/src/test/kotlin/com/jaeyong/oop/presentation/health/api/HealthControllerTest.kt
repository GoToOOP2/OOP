package com.jaeyong.oop.presentation.health.api

import com.jaeyong.oop.application.health.result.HealthCheckResult
import com.jaeyong.oop.application.health.usecase.HealthCheckUseCase
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(HealthController::class)
class HealthControllerTest {

    @SpringBootApplication
    class TestApplication

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var healthCheckUseCase: HealthCheckUseCase

    @Test
    @DisplayName("GET /api/health 호출 시 성공 응답을 반환해야 한다")
    fun `healthCheck should return success response`() {
        // given
        given(healthCheckUseCase.checkHealth()).willReturn(HealthCheckResult(status = "success"))

        // when & then
        mockMvc.perform(get("/api/health")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data").value("success"))
    }
}
