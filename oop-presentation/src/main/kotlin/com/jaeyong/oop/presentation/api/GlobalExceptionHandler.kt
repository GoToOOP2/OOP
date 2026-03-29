package com.jaeyong.oop.presentation.api

import com.jaeyong.oop.domain.exception.DomainException
import com.jaeyong.oop.domain.exception.InvalidBalanceException
import com.jaeyong.oop.domain.exception.UserNotFoundException
import com.jaeyong.oop.presentation.api.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 전역 예외 처리기 (Global Exception Handler)
 * 도메인의 비즈니스 예외(DomainException)를 잡아 ErrorCode로 번역합니다.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * 사용자를 찾을 수 없는 예외 처리 (UserNotFoundException -> U001)
     */
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(e: UserNotFoundException): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.error<Nothing>(ErrorCode.USER_NOT_FOUND.message)
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    /**
     * 잔액 부족 예외 처리 (InvalidBalanceException -> B001)
     */
    @ExceptionHandler(InvalidBalanceException::class)
    fun handleInvalidBalance(e: InvalidBalanceException): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.error<Nothing>(ErrorCode.INVALID_BALANCE.message)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    /**
     * 기타 모든 비즈니스 예외 처리 (DomainException)
     */
    @ExceptionHandler(DomainException::class)
    fun handleDomainException(e: DomainException): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.error<Nothing>(e.message ?: ErrorCode.INVALID_INPUT_VALUE.message)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    /**
     * 예상치 못한 서버 내부 오류 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.error<Nothing>(ErrorCode.INTERNAL_SERVER_ERROR.message)
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
