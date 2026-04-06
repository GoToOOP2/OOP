package com.jaeyong.oop.presentation.api

/**
 * [사용자 요청 반영]
 * HTTP 상태 코드가 이미 성공/실패 여부를 알려주므로, 
 * 중복된 'success: Boolean' 필드를 제거했습니다.
 * 
 * @param message 에러 발생 시의 설명 (성공 시 null)
 * @param data 성공 시의 실제 응답 데이터 (에러 시 null)
 */
data class ApiResponse<T>(
    val code: String? = null,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T?): ApiResponse<T> = ApiResponse(data = data)

        fun <T> error(code: String): ApiResponse<T> = ApiResponse(code = code)
    }
}
