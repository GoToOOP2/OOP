package com.jaeyong.oop.presentation.api.exception

/**
 * 프레젠테이션 계층(어댑터)에서 정의하는 외부 응답용 에러 코드입니다.
 * 도메인의 비즈니스 예외 상황을 외부 시스템 규격으로 번역할 때 사용됩니다.
 */
enum class ErrorCode(val code: String, val message: String) {
    // 공통 에러
    INVALID_INPUT_VALUE("C001", "잘못된 입력 값입니다."),
    INTERNAL_SERVER_ERROR("C002", "서버 내부 오류가 발생했습니다."),
    
    // 비즈니스 매핑 에러
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    INVALID_BALANCE("B001", "잔액이 부족합니다.")
}
