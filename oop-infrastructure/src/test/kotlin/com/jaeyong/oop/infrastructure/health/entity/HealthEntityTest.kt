package com.jaeyong.oop.infrastructure.health.entity

import com.jaeyong.oop.domain.health.Health
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class HealthEntityTest {

    @Test
    @DisplayName("1 & 2. 생성자 및 Getter 검증: 모든 필드가 정상적으로 초기화되고 값을 반환해야 한다")
    fun `constructor and getter validation`() {
        val id = 1L
        val status = "OK"
        val createdAt = LocalDateTime.now()
        val entity = HealthEntity(id = id, status = status, createdAt = createdAt)
        assertThat(entity.id).isEqualTo(id)
        assertThat(entity.status).isEqualTo(status)
        assertThat(entity.createdAt).isEqualTo(createdAt)
    }

    @Test
    @DisplayName("3. toDomain() 변환 검증: Entity가 Domain 객체로 정확히 매핑되어야 한다")
    fun `toDomain mapping validation`() {
        val id = 100L
        val status = "RUNNING"
        val createdAt = LocalDateTime.of(2026, 4, 7, 10, 0)
        val entity = HealthEntity(id = id, status = status, createdAt = createdAt)
        val domain = entity.toDomain()
        assertThat(domain).isNotNull
        assertThat(domain).isInstanceOf(Health::class.java)
        assertThat(domain.id).isEqualTo(entity.id)
        assertThat(domain.status).isEqualTo(entity.status)
        assertThat(domain.createdAt).isEqualTo(entity.createdAt)
    }

    @Test
    @DisplayName("4. 데이터 무결성 검증: toDomain() 호출 후에도 Entity 값은 변하지 않아야 하며 결과는 일관되어야 한다")
    fun `data integrity and idempotency validation`() {
        val entity = HealthEntity(id = 1L, status = "OK", createdAt = LocalDateTime.now())
        val firstDomain = entity.toDomain()
        val secondDomain = entity.toDomain()
        assertThat(entity.id).isEqualTo(1L)
        assertThat(entity.status).isEqualTo("OK")
        assertThat(firstDomain.id).isEqualTo(secondDomain.id)
        assertThat(firstDomain.status).isEqualTo(secondDomain.status)
        assertThat(firstDomain.createdAt).isEqualTo(secondDomain.createdAt)
    }

    @Test
    @DisplayName("5. 상태값(status) 검증: 다양한 상태값이 올바르게 유지되어야 한다")
    fun `status value validation`() {
        val okEntity = HealthEntity(status = "OK", createdAt = LocalDateTime.now())
        val failEntity = HealthEntity(status = "FAIL", createdAt = LocalDateTime.now())
        val unknownEntity = HealthEntity(status = "UNKNOWN_123", createdAt = LocalDateTime.now())
        assertThat(okEntity.status).isEqualTo("OK")
        assertThat(failEntity.status).isEqualTo("FAIL")
        assertThat(unknownEntity.status).isEqualTo("UNKNOWN_123")
    }

    @Test
    @DisplayName("경계값 검증: ID가 null인 경우에도 객체 생성이 가능해야 한다 (신규 저장 전 상태)")
    fun `boundary value validation - null id`() {
        val status = "NEW"
        val createdAt = LocalDateTime.now()
        val entity = HealthEntity(id = null, status = status, createdAt = createdAt)
        assertThat(entity.id).isNull()
        assertThat(entity.status).isEqualTo(status)
        val domain = entity.toDomain()
        assertThat(domain.id).isNull()
    }
}
