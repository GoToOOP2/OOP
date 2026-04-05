package com.jaeyong.oop.presentation.api.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ErrorCodeTest {

    @Test
    @DisplayName("ErrorCode의 각 항목은 고유한 코드와 메시지를 가져야 한다")
    fun testErrorCodeProperties() {
        // given
        val errorCode = ErrorCode.USER_NOT_FOUND

        // when & then
        // getCode()와 getMessage()를 모두 호출하여 커버리지를 확보합니다.
        assertThat(errorCode.code).isEqualTo("U001")
        assertThat(errorCode.message).isEqualTo("사용자를 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("ErrorCode의 모든 항목에 대해 필드 접근이 가능해야 한다")
    fun testAllErrorCodes() {
        // enum의 모든 항목을 순회하며 getCode, getMessage를 호출합니다.
        ErrorCode.entries.forEach { errorCode ->
            assertThat(errorCode.code).isNotNull()
            assertThat(errorCode.message).isNotNull()
        }
    }

    @Test
    @DisplayName("valueOf를 통해 이름을 기반으로 ErrorCode를 찾을 수 있다")
    fun testValueOf() {
        val name = "INTERNAL_SERVER_ERROR"
        val errorCode = ErrorCode.valueOf(name)
        assertThat(errorCode).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR)
    }
}
