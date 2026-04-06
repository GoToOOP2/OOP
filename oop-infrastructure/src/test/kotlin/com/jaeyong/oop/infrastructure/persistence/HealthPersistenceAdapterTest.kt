package com.jaeyong.oop.infrastructure.persistence

import com.jaeyong.oop.domain.health.Health
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.ArgumentMatchers.any
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class HealthPersistenceAdapterTest {

    @Mock
    private lateinit var healthJpaRepository: HealthJpaRepository

    @InjectMocks
    private lateinit var healthPersistenceAdapter: HealthPersistenceAdapter

    @Test
    @DisplayName("도메인 객체 Health를 저장하면 Repository를 통해 저장하고 결과를 반환해야 한다")
    fun `save should persist Health entity and return domain object`() {
        // given
        val now = LocalDateTime.now()
        val health = Health(status = "UP", createdAt = now)
        val entity = HealthEntity(id = 1L, status = "UP", createdAt = now)
        given(healthJpaRepository.save(any(HealthEntity::class.java))).willReturn(entity)

        // when
        val savedHealth = healthPersistenceAdapter.save(health)

        // then
        assertThat(savedHealth.id).isEqualTo(1L)
        assertThat(savedHealth.status).isEqualTo("UP")
        assertThat(savedHealth.createdAt).isEqualTo(now)
    }
}
