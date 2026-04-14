package com.jaeyong.oop.domain.health.port

import com.jaeyong.oop.domain.health.Health

interface HealthPort {

    /**
     * Health 상태를 저장하고 저장된 결과를 반환한다.
     *
     * @param health 저장할 Health 도메인 객체
     * @return 저장된 Health 도메인 객체
     */
    fun save(health: Health): Health
}
