package com.oop.common.exception

/**
 * 시스템 전체 에러코드 중앙 정의 (Single Source of Truth)
 *
 * 모든 레이어의 예외는 이 enum을 참조하여 에러코드를 지정한다.
 * 에러코드 추가 시 이 파일 하나만 수정하면 된다.
 * 에러 메시지는 에러코드 명세서로 관리한다.
 *
 * 에러코드 규칙:
 *   D-XXX : 도메인 비즈니스 에러
 *   A-XXX : 애플리케이션 에러
 *   C-XXX : 공통(프레젠테이션·인프라) 에러
 *   S-XXX : 시스템 에러
 */
enum class ErrorCode(
    val code: String
) {
    // ── 도메인 비즈니스 에러 (D-XXX) ──
    NOT_FOUND("D001"),
    DUPLICATE("D002"),
    BUSINESS_RULE_VIOLATION("D003"),
    INVALID_STATE("D004"),

    // ── 애플리케이션 에러 (A-XXX) ──
    UNAUTHORIZED("A001"),
    EXTERNAL_SERVICE_FAIL("A002"),

    // ── 공통 에러 (C-XXX) ──
    VALIDATION_ERROR("C001"),
    METHOD_NOT_ALLOWED("C002"),
    UNSUPPORTED_MEDIA_TYPE("C003"),

    // ── 시스템 에러 (S-XXX) ──
    INTERNAL_SERVER_ERROR("S001"),
}
