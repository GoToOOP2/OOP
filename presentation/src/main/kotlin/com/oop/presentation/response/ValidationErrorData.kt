package com.oop.presentation.response

/**
 * Validation 에러 응답의 data 래퍼
 *
 * ApiResponse<ValidationErrorData> 형태로 반환되어
 * 성공/실패 응답 구조를 동일하게 유지한다.
 */
data class ValidationErrorData(
    val errors: List<FieldError>
)

