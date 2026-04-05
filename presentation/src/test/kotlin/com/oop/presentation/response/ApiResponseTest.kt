package com.oop.presentation.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ApiResponseTest {

    @Test
    @DisplayName("success - 기본값: status=200, code=SUCCESS, data 존재")
    fun `success 기본값 호출 시 status 200과 code SUCCESS와 data를 반환한다`() {
        // Given
        val data = "hello"

        // When
        val result = ApiResponse.success(data = data)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertEquals("SUCCESS", result.body!!.code)
        assertEquals("hello", result.body!!.data)
    }

    @Test
    @DisplayName("success - 커스텀 status: CREATED(201) 반영")
    fun `success 호출 시 커스텀 status가 반영된다`() {
        // Given
        val data = "created"
        val status = HttpStatus.CREATED

        // When
        val result = ApiResponse.success(data = data, status = status)

        // Then
        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals("SUCCESS", result.body!!.code)
        assertEquals("created", result.body!!.data)
    }

    @Test
    @DisplayName("fail - status, code 반영, data=null")
    fun `fail 호출 시 status와 code 반환하고 data는 null이다`() {
        // Given
        val status = HttpStatus.BAD_REQUEST
        val code = "D001"

        // When
        val result = ApiResponse.fail(status = status, code = code)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertNotNull(result.body)
        assertEquals("D001", result.body!!.code)
        assertNull(result.body!!.data)
    }
}
