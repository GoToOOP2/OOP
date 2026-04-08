package com.jaeyong.oop.infrastructure.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class BcryptAdapterTest {
    private val bcryptAdapter = BcryptAdapter()

    @Test
    @DisplayName("비밀번호를 해싱하면 원본과 달라야 한다")
    fun `비밀번호를 해싱하면 원본과 달라야 한다`() {
        // given
        val rawPassword = "password123"

        // when
        val encoded = bcryptAdapter.encode(rawPassword)

        // then
        assertThat(encoded).isNotEqualTo(rawPassword)
    }

    @Test
    @DisplayName("원본 비밀번호와 해싱된 비밀번호가 일치해야 한다")
    fun `원본 비밀번호와 해싱된 비밀번호가 일치한다`() {
        // given
        val rawPassword = "password123"
        val encoded = bcryptAdapter.encode(rawPassword)

        // when & then
        assertThat(bcryptAdapter.matches(rawPassword, encoded)).isTrue()
    }

    @Test
    @DisplayName("같은 비밀번호를 두 번 해싱하면 서로 다른 해시값이 나와야 한다")
    fun `같은 비밀번호를 두 번 해싱하면 서로 다른 해시값이 나온다`() {
        // given
        val rawPassword = "password123"

        // when
        val encoded1 = bcryptAdapter.encode(rawPassword)
        val encoded2 = bcryptAdapter.encode(rawPassword)

        // then
        assertThat(encoded1).isNotEqualTo(encoded2)
    }

    @Test
    @DisplayName("틀린 비밀번호는 일치하지 않아야 한다")
    fun `틀린 비밀번호는 일치하지 않는다`() {
        // given
        val encoded = bcryptAdapter.encode("password123")

        // when & then
        assertThat(bcryptAdapter.matches("wrongPassword", encoded)).isFalse()
    }
}
