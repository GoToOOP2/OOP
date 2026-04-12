package com.jaeyong.oop.infrastructure.user.adapter

import com.jaeyong.oop.domain.user.vo.EncodedPasswordVO
import com.jaeyong.oop.domain.user.vo.RawPasswordVO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PasswordEncryptorAdapterTest {

    private val sut = PasswordEncryptorAdapter()

    @Test
    @DisplayName("1. encrypt 호출 시 BCrypt 해시를 반환한다")
    fun `encrypt 호출 시 BCrypt 해시를 반환한다`() {
        // when
        val encoded = sut.encrypt(RawPasswordVO("password123"))

        // then
        assertThat(encoded.value).isNotBlank()
        assertThat(encoded.value).isNotEqualTo("password123")
        assertThat(encoded.value).startsWith("\$2a\$")
    }

    @Test
    @DisplayName("2. matches - 원문과 해시가 일치하면 true를 반환한다")
    fun `matches - 원문과 해시가 일치하면 true를 반환한다`() {
        // given
        val encoded = sut.encrypt(RawPasswordVO("password123"))

        // when & then
        assertThat(sut.matches(RawPasswordVO("password123"), encoded)).isTrue()
    }

    @Test
    @DisplayName("3. matches - 원문과 해시가 일치하지 않으면 false를 반환한다")
    fun `matches - 원문과 해시가 일치하지 않으면 false를 반환한다`() {
        // given
        val encoded = sut.encrypt(RawPasswordVO("password123"))

        // when & then
        assertThat(sut.matches(RawPasswordVO("wrongpassword"), encoded)).isFalse()
    }
}
