package com.oop.presentation.exception

import com.oop.common.exception.BaseException
import com.oop.common.exception.ErrorCode
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

class GlobalExceptionHandlerTest {

    private lateinit var mockMvc: MockMvc

    @RestController
    class TestController {

        @GetMapping("/test/base-exception")
        fun baseException() {
            throw BaseException(ErrorCode.NOT_FOUND)
        }

        @GetMapping("/test/base-exception-custom")
        fun baseExceptionCustomMessage() {
            throw BaseException(ErrorCode.NOT_FOUND, "커스텀 메시지")
        }

        @PostMapping("/test/validation")
        fun validation(@Valid @RequestBody request: TestRequest): String = "ok"

        @PostMapping("/test/body")
        fun body(@RequestBody request: TestRequest): String = "ok"

        @GetMapping("/test/get-only")
        fun getOnly(): String = "ok"

        @GetMapping("/test/unexpected")
        fun unexpected() {
            throw RuntimeException("unexpected error")
        }
    }

    data class TestRequest(
        @field:NotBlank(message = "이름은 필수입니다")
        val name: String = ""
    )

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(TestController())
            .setControllerAdvice(GlobalExceptionHandler())
            .setValidator(LocalValidatorFactoryBean().apply { afterPropertiesSet() })
            .build()
    }

    // -- 1. BaseException --

    @Test
    @DisplayName("BaseException - 기본 메시지: status=400, code=D001")
    fun `BaseException 발생 시 400 상태와 해당 에러코드를 반환한다`() {
        // Given
        val request = get("/test/base-exception")

        // When
        val result = mockMvc.perform(request)

        // Then
        result
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("D001"))
            .andExpect(jsonPath("$.message").value("리소스를 찾을 수 없습니다"))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    @Test
    @DisplayName("BaseException - 커스텀 메시지: 전달한 메시지가 응답에 반영")
    fun `BaseException에 커스텀 메시지를 전달하면 해당 메시지가 응답에 반영된다`() {
        // Given
        val request = get("/test/base-exception-custom")

        // When
        val result = mockMvc.perform(request)

        // Then
        result
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("D001"))
            .andExpect(jsonPath("$.message").value("커스텀 메시지"))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    // -- 2. MethodArgumentNotValidException (@Valid) --

    @Test
    @DisplayName("Validation 실패 - status=400, code=C001, data.errors에 필드 정보 포함")
    fun `Valid 검증 실패 시 400 상태와 필드별 에러 정보를 반환한다`() {
        // Given
        val request = post("/test/validation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"name": " "}""")

        // When
        val result = mockMvc.perform(request)

        // Then
        result
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("C001"))
            .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다"))
            .andExpect(jsonPath("$.data.errors").isArray)
            .andExpect(jsonPath("$.data.errors[0].field").value("name"))
            .andExpect(jsonPath("$.data.errors[0].reason").value("이름은 필수입니다"))
    }

    // -- 3. HttpMessageNotReadableException --

    @Test
    @DisplayName("Body 파싱 실패 - status=400, code=C001")
    fun `잘못된 JSON 요청 시 400 상태와 검증 에러코드를 반환한다`() {
        // Given
        val request = post("/test/body")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ invalid json }")

        // When
        val result = mockMvc.perform(request)

        // Then
        result
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("C001"))
            .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다"))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    // -- 4. HttpRequestMethodNotSupportedException --

    @Test
    @DisplayName("잘못된 HTTP 메서드 - status=405, code=C002")
    fun `허용되지 않은 HTTP 메서드 요청 시 405 상태를 반환한다`() {
        // Given
        val request = delete("/test/get-only")

        // When
        val result = mockMvc.perform(request)

        // Then
        result
            .andExpect(status().isMethodNotAllowed)
            .andExpect(jsonPath("$.code").value("C002"))
            .andExpect(jsonPath("$.message").value("허용되지 않은 HTTP 메서드입니다"))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    // -- 5. HttpMediaTypeNotSupportedException --

    @Test
    @DisplayName("지원하지 않는 Content-Type - status=415, code=C003")
    fun `지원하지 않는 Content-Type 요청 시 415 상태를 반환한다`() {
        // Given
        val request = post("/test/validation")
            .contentType(MediaType.TEXT_PLAIN)
            .content("hello")

        // When
        val result = mockMvc.perform(request)

        // Then
        result
            .andExpect(status().isUnsupportedMediaType)
            .andExpect(jsonPath("$.code").value("C003"))
            .andExpect(jsonPath("$.message").value("지원하지 않는 Content-Type입니다"))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    // -- 6. Unexpected Exception --

    @Test
    @DisplayName("예상치 못한 예외 - status=500, code=S001")
    fun `예상치 못한 예외 발생 시 500 상태와 시스템 에러코드를 반환한다`() {
        // Given
        val request = get("/test/unexpected")

        // When
        val result = mockMvc.perform(request)

        // Then
        result
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.code").value("S001"))
            .andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다"))
            .andExpect(jsonPath("$.data").doesNotExist())
    }
}
