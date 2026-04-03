package com.oop.common.exception

/**
 * 시스템 전체 에러코드 중앙 정의 (Single Source of Truth)
 *
 * 모든 레이어의 예외는 이 enum을 참조하여 에러코드를 지정한다.
 * 에러코드 추가 시 이 파일 하나만 수정하면 된다.
 *
 * 에러코드 규칙:
 *   D-XXX : 도메인 비즈니스 에러
 *   A-XXX : 애플리케이션 에러
 *   C-XXX : 공통(프레젠테이션·인프라) 에러
 *   S-XXX : 시스템 에러
 */
enum class ErrorCode(
    val code: String,
    val defaultMessage: String
) {
    // ── 도메인 비즈니스 에러 (D-XXX) ──
    NOT_FOUND("D001", "리소스를 찾을 수 없습니다"),
    DUPLICATE("D002", "이미 존재하는 리소스입니다"),
    BUSINESS_RULE_VIOLATION("D003", "비즈니스 규칙에 위배됩니다"),
    INVALID_STATE("D004", "처리할 수 없는 상태입니다"),

    // ── 애플리케이션 에러 (A-XXX) ──
    UNAUTHORIZED("A001", "접근 권한이 없습니다"),
    EXTERNAL_SERVICE_FAIL("A002", "외부 서비스 호출에 실패했습니다"),

    // ── 공통 에러 (C-XXX) ──
    VALIDATION_ERROR("C001", "입력값 검증에 실패했습니다"),
    METHOD_NOT_ALLOWED("C002", "허용되지 않은 HTTP 메서드입니다"),
    UNSUPPORTED_MEDIA_TYPE("C003", "지원하지 않는 Content-Type입니다"),

    // ── 시스템 에러 (S-XXX) ──
    INTERNAL_SERVER_ERROR("S001", "서버 내부 오류가 발생했습니다"),
}
