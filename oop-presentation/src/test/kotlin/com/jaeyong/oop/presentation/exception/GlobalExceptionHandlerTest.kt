package com.jaeyong.oop.presentation.exception

import com.jaeyong.oop.application.usecase.HealthCheckUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.presentation.api.HealthController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpMethod
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@ExtendWith(MockitoExtension::class)
class GlobalExceptionHandlerTest {

    private lateinit var mockMvc: MockMvc
    private val sut = GlobalExceptionHandler()

    @Mock
    private lateinit var healthCheckUseCase: HealthCheckUseCase

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(HealthController(healthCheckUseCase))
            .setControllerAdvice(sut)
            .build()
    }

    @Test
    @DisplayName("1. 비즈니스 예외 발생 시 400 에러 반환")
    fun `handle BaseException`() {
        given(healthCheckUseCase.checkHealth()).willThrow(BaseException(ErrorCode.NOT_FOUND))

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND.code))
    }

    @Test
    @DisplayName("2. @Valid 검증 실패 시 400 에러 반환 (validation)")
    fun `handle MethodArgumentNotValidException`() {
        val exception = Mockito.mock(MethodArgumentNotValidException::class.java)
        given(healthCheckUseCase.checkHealth()).willAnswer { throw exception }

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.code))
    }

    @Test
    @DisplayName("3. Body 파싱 실패 시 400 에러 반환 (request body 파싱 실패)")
    fun `handle HttpMessageNotReadableException`() {
        @Suppress("DEPRECATION")
        val exception = HttpMessageNotReadableException("Invalid JSON")
        given(healthCheckUseCase.checkHealth()).willAnswer { throw exception }

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_REQUEST_BODY.code))
    }

    @Test
    @DisplayName("4. 필수 파라미터 누락 시 400 에러 반환 (missing parameter)")
    fun `handle MissingServletRequestParameterException`() {
        val exception = MissingServletRequestParameterException("name", "String")
        given(healthCheckUseCase.checkHealth()).willAnswer { throw exception }

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(ErrorCode.MISSING_PARAMETER.code))
    }

    @Test
    @DisplayName("5. 파라미터 타입 불일치 시 400 에러 반환 (type mismatch)")
    fun `handle MethodArgumentTypeMismatchException`() {
        val exception = Mockito.mock(MethodArgumentTypeMismatchException::class.java)
        given(healthCheckUseCase.checkHealth()).willAnswer { throw exception }

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(ErrorCode.TYPE_MISMATCH.code))
    }

    @Test
    @DisplayName("6. PathVariable 누락 시 400 에러 반환 (path variable 없음)")
    fun `handle MissingPathVariableException`() {
        val exception = Mockito.mock(MissingPathVariableException::class.java)
        given(healthCheckUseCase.checkHealth()).willAnswer { throw exception }

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(ErrorCode.MISSING_PATH_VARIABLE.code))
    }

    @Test
    @DisplayName("7. 허용되지 않은 HTTP 메서드 호출 시 405 에러 반환 (method not allowed)")
    fun `handle HttpRequestMethodNotSupportedException`() {
        mockMvc.perform(post("/api/health"))
            .andExpect(status().isMethodNotAllowed)
            .andExpect(jsonPath("$.code").value(ErrorCode.METHOD_NOT_ALLOWED.code))
    }

    @Test
    @DisplayName("8. 지원하지 않는 Media Type 시 415 에러 반환 (media type not supported)")
    fun `handle HttpMediaTypeNotSupportedException`() {
        val exception = Mockito.mock(HttpMediaTypeNotSupportedException::class.java)
        given(healthCheckUseCase.checkHealth()).willAnswer { throw exception }

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isUnsupportedMediaType)
            .andExpect(jsonPath("$.code").value(ErrorCode.UNSUPPORTED_MEDIA_TYPE.code))
    }

    @Test
    @DisplayName("9. Accept 헤더 불일치 시 406 에러 반환 (not acceptable)")
    fun `handle HttpMediaTypeNotAcceptableException`() {
        val exception = Mockito.mock(HttpMediaTypeNotAcceptableException::class.java)
        given(healthCheckUseCase.checkHealth()).willAnswer { throw exception }

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isNotAcceptable)
            .andExpect(jsonPath("$.code").value(ErrorCode.NOT_ACCEPTABLE.code))
    }

    @Test
    @DisplayName("10. 존재하지 않는 리소스 호출 시 404 에러 반환 (no resource found)")
    fun `handle NoResourceFoundException`() {
        val exception = NoResourceFoundException(HttpMethod.GET, "/api/health")
        given(healthCheckUseCase.checkHealth()).willAnswer { throw exception }

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.code))
    }

    @Test
    @DisplayName("11. 예기치 못한 예외 발생 시 500 에러 반환 (unexpected exception)")
    fun `handle Exception`() {
        given(healthCheckUseCase.checkHealth()).willThrow(RuntimeException("Fatal error"))

        mockMvc.perform(get("/api/health"))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.code").value(ErrorCode.INTERNAL_SERVER_ERROR.code))
    }
}
