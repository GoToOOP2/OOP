package com.jaeyong.oop.presentation.api

import com.jaeyong.oop.domain.exception.Domain1Exception
import com.jaeyong.oop.domain.exception.Domain1ErrorType
import com.jaeyong.oop.domain.exception.Domain2Exception
import com.jaeyong.oop.domain.exception.Domain2ErrorType
import com.jaeyong.oop.presentation.api.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Domain1Exception::class)
    fun handleDomain1Exception(e: Domain1Exception): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = when (e.type) {
            Domain1ErrorType.USER_NOT_FOUND -> ErrorCode.USER_NOT_FOUND
            Domain1ErrorType.ALREADY_EXISTS -> ErrorCode.INVALID_INPUT_VALUE
        }
        return ResponseEntity(ApiResponse.error(errorCode.code), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Domain2Exception::class)
    fun handleDomain2Exception(e: Domain2Exception): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = when (e.type) {
            Domain2ErrorType.INSUFFICIENT_FUNDS -> ErrorCode.INVALID_BALANCE
            Domain2ErrorType.LIMIT_EXCEEDED -> ErrorCode.INVALID_INPUT_VALUE
        }
        return ResponseEntity(ApiResponse.error(errorCode.code), HttpStatus.BAD_REQUEST)
    }

    /**
     * Spring Framework가 던지는 예외 처리
     */
    @ExceptionHandler(
        MethodArgumentNotValidException::class,
        HttpMessageNotReadableException::class,
        MissingServletRequestParameterException::class,
        MethodArgumentTypeMismatchException::class
    )
    fun handleBadRequestException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity(ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE.code), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity(ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED.code), HttpStatus.METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleMediaTypeNotSupportedException(e: HttpMediaTypeNotSupportedException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity(ApiResponse.error(ErrorCode.UNSUPPORTED_MEDIA_TYPE.code), HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(e: NoResourceFoundException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND.code), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.code), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
