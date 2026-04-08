package com.jaeyong.oop.presentation.api.exception

import com.jaeyong.oop.common.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ErrorCodeTest {
    @Test
    @DisplayName("ErrorCode의 각 항목은 고유한 코드를 가져야 한다")
    fun testErrorCodeProperties() {
        val errorCode = ErrorCode.NOT_FOUND

        assertThat(errorCode.code).isEqualTo("D001")
    }

    @Test
    @DisplayName("ErrorCode의 모든 항목에 대해 코드 접근이 가능해야 한다")
    fun testAllErrorCodes() {
        ErrorCode.entries.forEach { errorCode ->
            assertThat(errorCode.code).isNotNull()
        }
    }

    @Test
    @DisplayName("ErrorCode의 코드는 중복되지 않아야 한다")
    fun testUniqueErrorCodes() {
        val codes = ErrorCode.entries.map { it.code }
        assertThat(codes).doesNotHaveDuplicates()
    }

    @Test
    @DisplayName("valueOf를 통해 이름을 기반으로 ErrorCode를 찾을 수 있다")
    fun testValueOf() {
        val name = "INTERNAL_SERVER_ERROR"
        val errorCode = ErrorCode.valueOf(name)
        assertThat(errorCode).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR)
    }
}
