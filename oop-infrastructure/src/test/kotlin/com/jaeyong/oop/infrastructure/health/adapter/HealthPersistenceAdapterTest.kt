package com.jaeyong.oop.infrastructure.health.adapter

import com.jaeyong.oop.domain.health.Health
import com.jaeyong.oop.infrastructure.health.entity.HealthEntity
import com.jaeyong.oop.infrastructure.health.repository.HealthEntityRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class HealthPersistenceAdapterTest {

    @Mock
    private lateinit var healthEntityRepository: HealthEntityRepository

    @InjectMocks
    private lateinit var sut: HealthPersistenceAdapter

    @Test
    @DisplayName("도메인 객체 Health를 저장하면 Repository를 통해 저장하고 결과를 반환해야 한다")
    fun `save should persist Health entity and return domain object`() {
        // given
        val now = LocalDateTime.now()
        val health = Health.reconstruct(null, "UP", now)
        val entity = HealthEntity(id = 1L, status = "UP", createdAt = now)
        given(healthEntityRepository.save(anyNonNull())).willReturn(entity)

        // when
        val savedHealth = sut.save(health)

        // then
        assertThat(savedHealth.id).isEqualTo(1L)
        assertThat(savedHealth.status).isEqualTo("UP")
        assertThat(savedHealth.createdAt).isEqualTo(now)
    }

    private fun <T> anyNonNull(): T {
        ArgumentMatchers.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}
