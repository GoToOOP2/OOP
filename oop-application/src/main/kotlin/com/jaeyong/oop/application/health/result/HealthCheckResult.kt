package com.jaeyong.oop.application.health.result

data class HealthCheckResult private constructor(val status: String) {
    companion object {
        fun of(status: String): HealthCheckResult = HealthCheckResult(status)
    }
}
