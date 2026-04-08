package com.jaeyong.oop.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    @DisplayName("1. 필드가 정상적으로 초기화된다")
    fun `필드가 정상적으로 초기화된다`() {
        // when
        val user = User(id = 1L, username = "jaeyong", password = "hashed")

        // then
        assertThat(user.id).isEqualTo(1L)
        assertThat(user.username).isEqualTo("jaeyong")
        assertThat(user.password).isEqualTo("hashed")
    }

    @Test
    @DisplayName("2. id 기본값은 null이다")
    fun `id 기본값은 null이다`() {
        // when
        val user = User(username = "jaeyong", password = "hashed")

        // then
        assertThat(user.id).isNull()
    }

    @Test
    @DisplayName("3. 동일한 값이면 equals가 true를 반환한다")
    fun `동일한 값이면 equals가 true를 반환한다`() {
        // given
        val a = User(id = 1L, username = "jaeyong", password = "hashed")
        val b = User(id = 1L, username = "jaeyong", password = "hashed")

        // when & then
        assertThat(a).isEqualTo(b)
        assertThat(a.hashCode()).isEqualTo(b.hashCode())
    }

    @Test
    @DisplayName("4. copy로 특정 필드만 변경할 수 있다")
    fun `copy로 특정 필드만 변경할 수 있다`() {
        // given
        val original = User(id = 1L, username = "jaeyong", password = "hashed")

        // when
        val copied = original.copy(password = "newhash")

        // then
        assertThat(copied.id).isEqualTo(1L)
        assertThat(copied.username).isEqualTo("jaeyong")
        assertThat(copied.password).isEqualTo("newhash")
    }
}
