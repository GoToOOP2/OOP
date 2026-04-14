package com.jaeyong.oop.domain.health

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class HealthTest {

    @Test
    @DisplayName("Health 도메인 객체를 생성할 때 기본값이 올바르게 설정되어야 한다")
    fun `should create health entity with default values`() {
        // given
        val status = "OK"

        // when
        val health = Health.ok()

        // then
        assertThat(health.status).isEqualTo(status)
        assertThat(health.id).isNull()
        assertThat(health.createdAt).isBeforeOrEqualTo(LocalDateTime.now())
    }

    @Test
    @DisplayName("Health 객체의 모든 필드를 지정하여 생성할 수 있어야 한다")
    fun `should create health entity with all fields`() {
        // given
        val id = 1L
        val status = "MAINTENANCE"
        val createdAt = LocalDateTime.of(2024, 1, 1, 0, 0)

        // when
        val health = Health.restore(id, status, createdAt)

        // then
        assertThat(health.id).isEqualTo(id)
        assertThat(health.status).isEqualTo(status)
        assertThat(health.createdAt).isEqualTo(createdAt)
    }
}
