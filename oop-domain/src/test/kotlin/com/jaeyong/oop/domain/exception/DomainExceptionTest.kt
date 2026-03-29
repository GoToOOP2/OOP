package com.jaeyong.oop.domain.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DomainExceptionTest {

    @Test
    @DisplayName("UserNotFoundException 생성 시 기본 메시지가 올바르게 담긴다")
    fun testUserNotFoundException() {
        val exception = UserNotFoundException()
        assertThat(exception.message).isEqualTo("사용자를 찾을 수 없습니다.")
        assertThat(exception is DomainException).isTrue()
    }

    @Test
    @DisplayName("InvalidBalanceException 생성 시 기본 메시지가 올바르게 담긴다")
    fun testInvalidBalanceException() {
        val exception = InvalidBalanceException()
        assertThat(exception.message).isEqualTo("잔액이 부족합니다.")
        assertThat(exception is DomainException).isTrue()
    }

    @Test
    @DisplayName("DomainException에 커스텀 메시지를 전달할 수 있다")
    fun testDomainExceptionCustomMessage() {
        val message = "커스텀 예외 메시지"
        val exception = DomainException(message)
        assertThat(exception.message).isEqualTo(message)
    }
}
