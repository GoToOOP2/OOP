package com.jaeyong.oop.presentation.api

import com.jaeyong.oop.domain.exception.DomainException
import com.jaeyong.oop.domain.exception.InvalidBalanceException
import com.jaeyong.oop.domain.exception.UserNotFoundException
import com.jaeyong.oop.presentation.api.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    @DisplayName("UserNotFoundException 발생 시 HTTP 404와 USER_NOT_FOUND 에러 코드를 반환한다")
    fun handleUserNotFound() {
        val exception = UserNotFoundException()
        val response = handler.handleUserNotFound(exception)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body?.success).isFalse()
        assertThat(response.body?.message).isEqualTo(ErrorCode.USER_NOT_FOUND.message)
    }

    @Test
    @DisplayName("InvalidBalanceException 발생 시 HTTP 400과 INVALID_BALANCE 에러 코드를 반환한다")
    fun handleInvalidBalance() {
        val exception = InvalidBalanceException()
        val response = handler.handleInvalidBalance(exception)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.success).isFalse()
        assertThat(response.body?.message).isEqualTo(ErrorCode.INVALID_BALANCE.message)
    }

    @Test
    @DisplayName("기타 도메인 예외 발생 시 HTTP 400과 해당 예외 메시지를 반환한다")
    fun handleOtherDomainException() {
        val message = "기타 도메인 에러"
        val exception = DomainException(message)
        val response = handler.handleDomainException(exception)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.success).isFalse()
        assertThat(response.body?.message).isEqualTo(message)
    }

    @Test
    @DisplayName("메시지가 없는 도메인 예외 발생 시 기본 에러 메시지를 반환한다 (Branch 커버리지용)")
    fun handleDomainExceptionWithNullMessage() {
        // given: 메시지가 null인 DomainException 생성
        val exception = object : DomainException("") {
            override val message: String? = null
        }

        // when
        val response = handler.handleDomainException(exception)

        // then: ErrorCode.INVALID_INPUT_VALUE의 메시지가 반환되어야 함
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.success).isFalse()
        assertThat(response.body?.message).isEqualTo(ErrorCode.INVALID_INPUT_VALUE.message)
    }

    @Test
    @DisplayName("예상치 못한 시스템 예외 발생 시 HTTP 500과 서버 내부 오류 메시지를 반환한다")
    fun handleSystemException() {
        val exception = RuntimeException("Unknown system error")
        val response = handler.handleException(exception)

        assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(response.body?.success).isFalse()
        assertThat(response.body?.message).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.message)
    }
}
