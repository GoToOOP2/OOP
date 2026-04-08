package com.jaeyong.oop.domain.member

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MemberTest {
    @Test
    @DisplayName("Member 도메인 객체를 생성할 때 기본값이 올바르게 설정되어야 한다")
    fun `기본값으로 Member 객체를 생성할 수 있다`() {
        // given
        val email = "test@example.com"
        val password = "hashedPassword"
        val nickname = "테스트"

        // when
        val member = Member(email = email, password = password, nickname = nickname)

        // then
        assertThat(member.id).isNull()
        assertThat(member.email).isEqualTo(email)
        assertThat(member.password).isEqualTo(password)
        assertThat(member.nickname).isEqualTo(nickname)
    }

    @Test
    @DisplayName("Member 객체의 모든 필드를 지정하여 생성할 수 있어야 한다 (반환할때 ID 채워서 반환 해줘야함)")
    fun `모든 필드를 지정하여 Member 객체를 생성할 수 있다`() {
        // given & when
        val member = Member(id = 1L, email = "test@example.com", password = "hashed", nickname = "닉네임")

        // then
        assertThat(member.id).isEqualTo(1L)
    }
}
