package com.jaeyong.oop.application.health.result

import kotlin.ConsistentCopyVisibility

/**
 * 헬스 체크 유스케이스 결과.
 *
 * @property status 서버 상태 문자열 (예: "success")
 */
@ConsistentCopyVisibility
data class HealthCheckResult private constructor(val status: String) {
    companion object {
        fun of(status: String): HealthCheckResult = HealthCheckResult(status)
    }
}
