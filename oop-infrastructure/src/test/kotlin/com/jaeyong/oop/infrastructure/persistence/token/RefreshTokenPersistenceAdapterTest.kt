package com.jaeyong.oop.infrastructure.persistence.token

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
@Import(RefreshTokenPersistenceAdapter::class)
class RefreshTokenPersistenceAdapterTest {
    @Autowired
    private lateinit var adapter: RefreshTokenPersistenceAdapter

    @Autowired
    private lateinit var refreshTokenJpaRepository: RefreshTokenJpaRepository

    @Test
    @DisplayName("Refresh Token을 저장하고 조회할 수 있어야 한다")
    fun `Refresh Token을 저장하고 조회할 수 있다`() {
        // given
        adapter.save(1L, "hashed-token")

        // when
        val result = adapter.findByMemberId(1L)

        // then
        assertThat(result).isEqualTo("hashed-token")
    }

    @Test
    @DisplayName("같은 회원의 Refresh Token을 저장하면 기존 토큰이 갱신되어야 한다")
    fun `같은 회원의 Refresh Token을 저장하면 기존 토큰이 갱신된다`() {
        // given
        adapter.save(1L, "old-token")

        // when
        adapter.save(1L, "new-token")

        // then
        val result = adapter.findByMemberId(1L)
        assertThat(result).isEqualTo("new-token")

        // 레코드가 1개만 존재하는지 확인 (Upsert)
        assertThat(refreshTokenJpaRepository.findAll()).hasSize(1)
    }

    @Test
    @DisplayName("존재하지 않는 회원의 Refresh Token 조회 시 null을 반환해야 한다")
    fun `존재하지 않는 회원의 Refresh Token 조회 시 null을 반환한다`() {
        // when
        val result = adapter.findByMemberId(999L)

        // then
        assertThat(result).isNull()
    }

    @Test
    @DisplayName("Refresh Token을 삭제하면 조회되지 않아야 한다")
    fun `Refresh Token을 삭제하면 조회되지 않는다`() {
        // given
        adapter.save(1L, "hashed-token")

        // when
        adapter.deleteByMemberId(1L)

        // then
        assertThat(adapter.findByMemberId(1L)).isNull()
    }

    @Test
    @DisplayName("서로 다른 회원의 Refresh Token은 독립적으로 관리되어야 한다")
    fun `서로 다른 회원의 Refresh Token은 독립적으로 관리된다`() {
        // given
        adapter.save(1L, "token-1")
        adapter.save(2L, "token-2")

        // when
        adapter.deleteByMemberId(1L)

        // then
        assertThat(adapter.findByMemberId(1L)).isNull()
        assertThat(adapter.findByMemberId(2L)).isEqualTo("token-2")
    }
}
