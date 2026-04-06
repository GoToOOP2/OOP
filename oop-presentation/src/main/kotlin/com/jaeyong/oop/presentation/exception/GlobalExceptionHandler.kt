package com.jaeyong.oop.presentation.exception

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.presentation.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * 전역 예외 처리기
 *
 * 모든 예외를 ApiResponse 포멧으로 변환하여 일관된 응답 구조를 보장한다.
 *
 * HTTP 상태 코드와 에러코드는 별개의 관심사이다.
 *   - HTTP 상태: 프로토콜 수준의 응답 분류 (헤더)
 *   - 에러코드: "이 코드면 이런 문제" 라는 우리의 약속 (Body)
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    // ── 1. 비즈니스 예외 (BaseException 하위) → 400 ──
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("[{}]", e.errorCode.code)
        return ApiResponse.fail(HttpStatus.BAD_REQUEST, e.errorCode.code)
    }

    // ── 2. @Valid 검증 실패 → 400 ──
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("[{}]", ErrorCode.VALIDATION_ERROR.code)
        return ApiResponse.fail(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR.code)
    }

    // ── 3. 요청 Body 파싱 실패 → 400 ──
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidRequestBody(e: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("[{}]", ErrorCode.INVALID_REQUEST_BODY.code)
        return ApiResponse.fail(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST_BODY.code)
    }

    // ── 4. 필수 쿼리 파라미터 누락 → 400 ──
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameter(e: MissingServletRequestParameterException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("[{}]", ErrorCode.MISSING_PARAMETER.code)
        return ApiResponse.fail(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PARAMETER.code)
    }

    // ── 5. 파라미터 타입 변환 실패 → 400 ──
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(e: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("[{}]", ErrorCode.TYPE_MISMATCH.code)
        return ApiResponse.fail(HttpStatus.BAD_REQUEST, ErrorCode.TYPE_MISMATCH.code)
    }

    // ── 6. PathVariable 누락 → 400 ──
    @ExceptionHandler(MissingPathVariableException::class)
    fun handleMissingPathVariable(e: MissingPathVariableException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("[{}]", ErrorCode.MISSING_PATH_VARIABLE.code)
        return ApiResponse.fail(HttpStatus.BAD_REQUEST, ErrorCode.MISSING_PATH_VARIABLE.code)
    }

    // ── 7. 잘못된 HTTP 메서드 → 405 ──
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(e: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Nothing>> {
        return ApiResponse.fail(HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED.code)
    }

    // ── 8. 지원하지 않는 Content-Type → 415 ──
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleUnsupportedMediaType(e: HttpMediaTypeNotSupportedException): ResponseEntity<ApiResponse<Nothing>> {
        return ApiResponse.fail(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorCode.UNSUPPORTED_MEDIA_TYPE.code)
    }

    // ── 9. Accept 헤더 불일치 → 406 ──
    @ExceptionHandler(HttpMediaTypeNotAcceptableException::class)
    fun handleNotAcceptable(e: HttpMediaTypeNotAcceptableException): ResponseEntity<ApiResponse<Nothing>> {
        return ApiResponse.fail(HttpStatus.NOT_ACCEPTABLE, ErrorCode.NOT_ACCEPTABLE.code)
    }

    // ── 10. 존재하지 않는 리소스 → 404 ──
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(e: NoResourceFoundException): ResponseEntity<ApiResponse<Nothing>> {
        return ApiResponse.fail(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND.code)
    }

    // ── 11. 그 외 모든 예외 → 500 ──
    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("[UNEXPECTED]", e)
        return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.code)
    }
}
