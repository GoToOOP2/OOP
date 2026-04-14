package com.jaeyong.oop.application.health.result

/**
 * 헬스 체크 유스케이스 결과.
 *
 * @property status 서버 상태 문자열 (예: "success")
 */
data class HealthCheckResult private constructor(val status: String) {
    companion object {
        fun of(status: String): HealthCheckResult = HealthCheckResult(status)
    }
}
