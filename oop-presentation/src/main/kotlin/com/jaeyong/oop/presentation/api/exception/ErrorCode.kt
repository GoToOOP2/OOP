package com.jaeyong.oop.presentation.api.exception

/**
 * 프레젠테이션 계층(어댑터)에서 정의하는 외부 응답용 에러 코드입니다.
 * 도메인의 비즈니스 예외 상황을 외부 시스템 규격으로 번역할 때 사용됩니다.
 */
enum class ErrorCode(val code: String) {
    // 공통 에러
    INVALID_INPUT_VALUE("C001"),
    INTERNAL_SERVER_ERROR("C002"),
    METHOD_NOT_ALLOWED("C003"),
    UNSUPPORTED_MEDIA_TYPE("C004"),
    RESOURCE_NOT_FOUND("C005"),

    // 비즈니스 매핑 에러
    USER_NOT_FOUND("U001"),
    INVALID_BALANCE("B001")
}
