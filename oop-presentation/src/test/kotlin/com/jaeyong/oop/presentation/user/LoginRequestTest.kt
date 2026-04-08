package com.jaeyong.oop.presentation.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class LoginRequestTest {

    @Test
    @DisplayName("1. 필드가 정상적으로 초기화된다")
    fun `필드가 정상적으로 초기화된다`() {
        // when
        val request = LoginRequest(username = "jaeyong", password = "password123")

        // then
        assertThat(request.username).isEqualTo("jaeyong")
        assertThat(request.password).isEqualTo("password123")
    }

    @Test
    @DisplayName("2. 동일한 값이면 equals가 true를 반환한다")
    fun `동일한 값이면 equals가 true를 반환한다`() {
        // given
        val a = LoginRequest(username = "jaeyong", password = "password123")
        val b = LoginRequest(username = "jaeyong", password = "password123")

        // when & then
        assertThat(a).isEqualTo(b)
        assertThat(a.hashCode()).isEqualTo(b.hashCode())
    }

    @Test
    @DisplayName("3. copy로 특정 필드만 변경할 수 있다")
    fun `copy로 특정 필드만 변경할 수 있다`() {
        // given
        val original = LoginRequest(username = "jaeyong", password = "password123")

        // when
        val copied = original.copy(password = "newpassword")

        // then
        assertThat(copied.username).isEqualTo("jaeyong")
        assertThat(copied.password).isEqualTo("newpassword")
    }
}
