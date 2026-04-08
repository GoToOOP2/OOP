package com.jaeyong.oop.common.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class BaseExceptionTest {
    @Test
    @DisplayName("BaseException 생성 시 설정한 ErrorCode와 메시지가 올바르게 저장되어야 한다")
    fun `should have correct error code when created`() {
        // given
        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR

        // when
        val exception = BaseException(errorCode)

        // then
        assertThat(exception.errorCode).isEqualTo(errorCode)
        assertThat(exception.message).isEqualTo(errorCode.code)
    }

    @Test
    @DisplayName("Cause(원인 예외)가 포함된 BaseException을 생성할 수 있어야 한다")
    fun `should contain cause when provided`() {
        // given
        val cause = IllegalArgumentException("Original cause")
        val errorCode = ErrorCode.INVALID_REQUEST_BODY

        // when
        val exception = BaseException(errorCode, cause)

        // then
        assertThat(exception.cause).isEqualTo(cause)
        assertThat(exception.errorCode).isEqualTo(errorCode)
    }
}
