package com.jaeyong.oop.application.health.usecase

import com.jaeyong.oop.application.health.result.HealthCheckResult

interface HealthCheckUseCase {

    /**
     * 서버 상태를 확인하고 결과를 반환한다.
     *
     * @return 현재 서버 상태 결과
     */
    fun checkHealth(): HealthCheckResult
}
