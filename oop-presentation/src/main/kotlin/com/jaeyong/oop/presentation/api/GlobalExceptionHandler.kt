package com.jaeyong.oop.presentation.api

import com.jaeyong.oop.domain.exception.Domain1Exception
import com.jaeyong.oop.domain.exception.Domain1ErrorType
import com.jaeyong.oop.domain.exception.Domain2Exception
import com.jaeyong.oop.domain.exception.Domain2ErrorType
import com.jaeyong.oop.presentation.api.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * [멘토님 의견 반영]
 * 예외를 기능(UseCase)별로 낱개로 만드는 대신, 
 * 도메인(Domain1, Domain2)별로 묶어서 관리하는 구조입니다.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * [도메인 1 에러 묶음 처리]
     * 회원(User)과 관련된 모든 에러 케이스를 여기서 관리합니다.
     */
    @ExceptionHandler(Domain1Exception::class)
    fun handleDomain1Exception(e: Domain1Exception): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = when (e.type) {
            Domain1ErrorType.USER_NOT_FOUND -> ErrorCode.USER_NOT_FOUND
            Domain1ErrorType.ALREADY_EXISTS -> ErrorCode.INVALID_INPUT_VALUE // 필요 시 새로운 ErrorCode 추가 가능
        }
        
        val response = ApiResponse.error<Nothing>(errorCode.message)
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    /**
     * [도메인 2 에러 묶음 처리]
     * 잔액(Balance)과 관련된 모든 에러 케이스를 여기서 관리합니다.
     */
    @ExceptionHandler(Domain2Exception::class)
    fun handleDomain2Exception(e: Domain2Exception): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = when (e.type) {
            Domain2ErrorType.INSUFFICIENT_FUNDS -> ErrorCode.INVALID_BALANCE
            Domain2ErrorType.LIMIT_EXCEEDED -> ErrorCode.INVALID_INPUT_VALUE // 필요 시 새로운 ErrorCode 추가 가능
        }
        
        val response = ApiResponse.error<Nothing>(errorCode.message)
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
