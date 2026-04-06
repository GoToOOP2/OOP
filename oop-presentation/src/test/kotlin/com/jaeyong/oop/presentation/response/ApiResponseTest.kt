package com.jaeyong.oop.presentation.response

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ApiResponseTest {

    @Test
    @DisplayName("success 응답 생성 시 SUCCESS 코드와 데이터가 포함되어야 한다")
    fun `success should create correct response`() {
        // given
        val data = "test data"

        // when
        val responseEntity = ApiResponse.success(data)

        // then
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body).isNotNull
        assertThat(responseEntity.body?.code).isEqualTo("SUCCESS")
        assertThat(responseEntity.body?.data).isEqualTo(data)
    }

    @Test
    @DisplayName("fail 응답 생성 시 입력한 상태 코드와 에러 코드가 포함되어야 한다")
    fun `fail should create correct response`() {
        // given
        val status = HttpStatus.BAD_REQUEST
        val errorCode = "D001"

        // when
        val responseEntity = ApiResponse.fail(status, errorCode)

        // then
        assertThat(responseEntity.statusCode).isEqualTo(status)
        assertThat(responseEntity.body).isNotNull
        assertThat(responseEntity.body?.code).isEqualTo(errorCode)
        
        // Use an explicit cast to avoid ambiguity with null
        val data: Any? = responseEntity.body?.data
        assertThat(data).isNull()
    }
}
