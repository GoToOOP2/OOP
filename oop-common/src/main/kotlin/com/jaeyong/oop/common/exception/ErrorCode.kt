package com.jaeyong.oop.common.exception

/**
 * 시스템 전체 에러코드 중앙 정의 (Single Source of Truth)
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
    /** D001 — 리소스를 찾을 수 없음 */
    NOT_FOUND("D001"),
    /** D002 — 중복된 리소스 */
    DUPLICATE("D002"),
    /** D003 — 비즈니스 규칙 위반 */
    BUSINESS_RULE_VIOLATION("D003"),
    /** D004 — 유효하지 않은 상태 */
    INVALID_STATE("D004"),

    /** A001 — 인증 실패 (자격증명 불일치, 토큰 무효) */
    UNAUTHORIZED("A001"),
    /** A002 — 외부 서비스 호출 실패 */
    EXTERNAL_SERVICE_FAIL("A002"),

    /** C001 — `@Valid` 검증 실패 */
    VALIDATION_ERROR("C001"),
    /** C002 — 허용되지 않는 HTTP 메서드 */
    METHOD_NOT_ALLOWED("C002"),
    /** C003 — 지원하지 않는 Content-Type */
    UNSUPPORTED_MEDIA_TYPE("C003"),
    /** C004 — Accept 헤더 불일치 */
    NOT_ACCEPTABLE("C004"),
    /** C005 — 존재하지 않는 리소스 경로 */
    RESOURCE_NOT_FOUND("C005"),
    /** C006 — 요청 Body 파싱 실패 */
    INVALID_REQUEST_BODY("C006"),
    /** C007 — 필수 쿼리 파라미터 누락 */
    MISSING_PARAMETER("C007"),
    /** C008 — 파라미터 타입 변환 실패 */
    TYPE_MISMATCH("C008"),
    /** C009 — PathVariable 누락 */
    MISSING_PATH_VARIABLE("C009"),

    /** S001 — 예상치 못한 서버 내부 오류 */
    INTERNAL_SERVER_ERROR("S001"),
}
