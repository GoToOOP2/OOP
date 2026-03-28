package com.jaeyong.oop.presentation.api

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T?): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = null,
                data = data
            )
        }

        fun <T> error(message: String?): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                data = null
            )
        }
    }
}
