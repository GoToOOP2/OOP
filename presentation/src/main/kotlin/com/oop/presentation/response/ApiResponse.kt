package com.oop.presentation.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * 공통응답 포멧
 *
 * 모든 API 응답은 이 클래스를 통해 동일한 구조로 반환된다.
 * HTTP 상태 코드는 헤더에서 확인하므로 Body에 포함하지 않는다.
 */
data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T?
) {
    companion object {

        private const val SUCCESS_CODE = "SUCCESS"
        private const val SUCCESS_MESSAGE = "성공"

        // ── 성공 응답 ──

        fun <T> success(
            data: T,
            status: HttpStatus = HttpStatus.OK,
            message: String = SUCCESS_MESSAGE
        ): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.status(status)
                .body(ApiResponse(SUCCESS_CODE, message, data))

        // ── 실패 응답 ──

        fun fail(
            status: HttpStatus, code: String, message: String
        ): ResponseEntity<ApiResponse<Nothing>> =
            ResponseEntity.status(status)
                .body(ApiResponse(code, message, null))

        // ── 실패 응답 (data 포함 — Validation 에러 등) ──

        fun <T> fail(
            status: HttpStatus, code: String, message: String, data: T
        ): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.status(status)
                .body(ApiResponse(code, message, data))
    }
}
