package com.oop.presentation.response

/**
 * Validation 에러 시 실패한 필드 정보
 */
data class FieldError(
    val field: String,
    val reason: String
)

