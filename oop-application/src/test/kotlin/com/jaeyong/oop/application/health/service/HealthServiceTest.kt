package com.jaeyong.oop.application.health.service

import com.jaeyong.oop.domain.health.Health
import com.jaeyong.oop.domain.health.port.HealthPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class HealthServiceTest {

    @Mock
    private lateinit var healthRepository: HealthPort

    @InjectMocks
    private lateinit var sut: HealthService

    @Test
    @DisplayName("checkHealth 호출 시 Health 도메인을 저장하고 HealthCheckResult를 반환해야 한다")
    fun `checkHealth should save health and return HealthCheckResult`() {
        // given
        val health = Health.ok()

        `when`(healthRepository.save(anyNonNull())).thenReturn(health)

        // when
        val result = sut.checkHealth()

        // then
        assertThat(result.status).isEqualTo("success")
        verify(healthRepository).save(anyNonNull())
    }

    private fun <T> anyNonNull(): T {
        ArgumentMatchers.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}
