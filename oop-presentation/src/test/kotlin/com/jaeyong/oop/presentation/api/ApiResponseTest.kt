package com.jaeyong.oop.presentation.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ApiResponseTest {

    @Test
    @DisplayName("성공 응답을 생성하면 success가 true이고 데이터가 포함된다")
    fun testSuccessResponse() {
        val data = "Hello"
        val response = ApiResponse.success(data)

        assertThat(response.success).isTrue()
        assertThat(response.data).isEqualTo(data)
        assertThat(response.message).isNull()
    }

    @Test
    @DisplayName("에러 응답을 생성하면 success가 false이고 메시지가 포함된다")
    fun testErrorResponse() {
        val errorMessage = "Error"
        val response = ApiResponse.error<String>(errorMessage)

        assertThat(response.success).isFalse()
        assertThat(response.message).isEqualTo(errorMessage)
        assertThat(response.data).isNull()
    }

    @Test
    @DisplayName("생성자의 기본값을 사용하여 객체를 생성한다 (특수 생성자 커버리지용)")
    fun testDefaultConstructor() {
        // message와 data를 생략하여 코틀린이 내부적으로 만든 특수 생성자를 호출합니다.
        // 이 테스트가 없으면 커버리지가 56%에 머무릅니다.
        val response = ApiResponse<String>(success = true)

        assertThat(response.success).isTrue()
        assertThat(response.message).isNull()
        assertThat(response.data).isNull()
    }
}
