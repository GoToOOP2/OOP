package com.oop.presentation.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ApiResponseTest {

    @Test
    @DisplayName("success - 기본값: status=200, code=SUCCESS, message=성공, data 존재")
    fun `success 기본값 호출 시 status 200과 code SUCCESS와 data를 반환한다`() {
        // Given
        val data = "hello"

        // When
        val result = ApiResponse.success(data = data)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertEquals("SUCCESS", result.body!!.code)
        assertEquals("성공", result.body!!.message)
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
    @DisplayName("success - 커스텀 message: 전달한 메시지 반영")
    fun `success 호출 시 커스텀 message가 반영된다`() {
        // Given
        val data = "hello"
        val message = "완료되었습니다"

        // When
        val result = ApiResponse.success(data = data, message = message)

        // Then
        assertEquals("완료되었습니다", result.body!!.message)
        assertEquals("SUCCESS", result.body!!.code)
        assertEquals("hello", result.body!!.data)
    }

    @Test
    @DisplayName("fail - data 없음: status, code, message 반영, data=null")
    fun `fail 호출 시 data 없이 status와 code와 message만 반환한다`() {
        // Given
        val status = HttpStatus.BAD_REQUEST
        val code = "D001"
        val message = "리소스를 찾을 수 없습니다"

        // When
        val result = ApiResponse.fail(status = status, code = code, message = message)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertNotNull(result.body)
        assertEquals("D001", result.body!!.code)
        assertEquals("리소스를 찾을 수 없습니다", result.body!!.message)
        assertNull(result.body!!.data)
    }

    @Test
    @DisplayName("fail - data 포함: ValidationErrorData가 정상적으로 포함")
    fun `fail 호출 시 ValidationErrorData가 data에 포함된다`() {
        // Given
        val errors = listOf(FieldError("name", "필수 입력값입니다"))
        val validationData = ValidationErrorData(errors)

        // When
        val result = ApiResponse.fail(
            status = HttpStatus.BAD_REQUEST,
            code = "C001",
            message = "입력값 검증에 실패했습니다",
            data = validationData
        )

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
        assertNotNull(result.body)
        assertEquals("C001", result.body!!.code)
        assertEquals("입력값 검증에 실패했습니다", result.body!!.message)
        assertNotNull(result.body!!.data)
        assertEquals(1, result.body!!.data!!.errors.size)
        assertEquals("name", result.body!!.data!!.errors[0].field)
        assertEquals("필수 입력값입니다", result.body!!.data!!.errors[0].reason)
    }
}
