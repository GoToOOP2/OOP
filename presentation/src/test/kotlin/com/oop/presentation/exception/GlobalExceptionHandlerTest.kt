package com.oop.presentation.exception

import com.oop.common.exception.BaseException
import com.oop.common.exception.ErrorCode
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    // -- 1. BaseException --

    @Test
    @DisplayName("BaseException 발생 시 400 상태와 해당 에러코드를 반환한다")
    fun handleBaseException() {
        val exception = BaseException(ErrorCode.NOT_FOUND)

        val result = handler.handleBaseException(exception)

        assertThat(result.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(result.body?.code).isEqualTo("D001")
        assertThat(result.body?.data as Any?).isNull()
    }

    // -- 2. MethodArgumentNotValidException --

    @Test
    @DisplayName("Validation 실패 시 400 상태와 C001 에러코드를 반환한다")
    fun handleValidation() {
        val exception = mockk<MethodArgumentNotValidException>()

        val result = handler.handleValidation(exception)

        assertThat(result.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(result.body?.code).isEqualTo("C001")
        assertThat(result.body?.data as Any?).isNull()
    }

    // -- 3. HttpMessageNotReadableException --

    @Test
    @DisplayName("Body 파싱 실패 시 400 상태와 C001 에러코드를 반환한다")
    fun handleNotReadable() {
        val exception = mockk<HttpMessageNotReadableException>()

        val result = handler.handleNotReadable(exception)

        assertThat(result.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(result.body?.code).isEqualTo("C001")
        assertThat(result.body?.data as Any?).isNull()
    }

    // -- 4. HttpRequestMethodNotSupportedException --

    @Test
    @DisplayName("허용되지 않은 HTTP 메서드 요청 시 405 상태를 반환한다")
    fun handleMethodNotAllowed() {
        val exception = HttpRequestMethodNotSupportedException("DELETE")

        val result = handler.handleMethodNotAllowed(exception)

        assertThat(result.statusCode).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
        assertThat(result.body?.code).isEqualTo("C002")
        assertThat(result.body?.data as Any?).isNull()
    }

    // -- 5. HttpMediaTypeNotSupportedException --

    @Test
    @DisplayName("지원하지 않는 Content-Type 요청 시 415 상태를 반환한다")
    fun handleUnsupportedMediaType() {
        val exception = HttpMediaTypeNotSupportedException("text/plain")

        val result = handler.handleUnsupportedMediaType(exception)

        assertThat(result.statusCode).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        assertThat(result.body?.code).isEqualTo("C003")
        assertThat(result.body?.data as Any?).isNull()
    }

    // -- 6. Unexpected Exception --

    @Test
    @DisplayName("예상치 못한 예외 발생 시 500 상태와 시스템 에러코드를 반환한다")
    fun handleUnexpected() {
        val exception = RuntimeException("unexpected")

        val result = handler.handleUnexpected(exception)

        assertThat(result.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(result.body?.code).isEqualTo("S001")
        assertThat(result.body?.data as Any?).isNull()
    }
}
