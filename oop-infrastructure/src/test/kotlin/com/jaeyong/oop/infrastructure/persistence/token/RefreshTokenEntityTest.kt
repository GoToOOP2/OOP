package com.jaeyong.oop.infrastructure.persistence.token

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class RefreshTokenEntityTest {
    @Test
    @DisplayName("RefreshTokenEntity를 생성할 수 있어야 한다")
    fun `RefreshTokenEntity를 생성할 수 있다`() {
        // given & when
        val entity = RefreshTokenEntity(memberId = 1L, hashedToken = "hashed-token")

        // then
        assertThat(entity.memberId).isEqualTo(1L)
        assertThat(entity.hashedToken).isEqualTo("hashed-token")
    }

    @Test
    @DisplayName("해싱된 토큰을 갱신할 수 있어야 한다")
    fun `해싱된 토큰을 갱신할 수 있다`() {
        // given
        val entity = RefreshTokenEntity(memberId = 1L, hashedToken = "old-token")

        // when
        entity.updateHashedToken("new-token")

        // then
        assertThat(entity.hashedToken).isEqualTo("new-token")
    }
}
