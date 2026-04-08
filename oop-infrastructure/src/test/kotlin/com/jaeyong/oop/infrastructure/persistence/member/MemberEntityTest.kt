package com.jaeyong.oop.infrastructure.persistence.member

import com.jaeyong.oop.domain.member.Member
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MemberEntityTest {
    @Test
    @DisplayName("도메인 Member를 MemberEntity로 변환할 수 있어야 한다")
    fun `도메인 Member를 MemberEntity로 변환할 수 있다`() {
        // given
        val member = Member(email = "test@example.com", password = "hashed", nickname = "닉네임")

        // when
        val entity = MemberEntity.fromDomain(member)

        // then
        assertThat(entity.email).isEqualTo("test@example.com")
        assertThat(entity.password).isEqualTo("hashed")
        assertThat(entity.nickname).isEqualTo("닉네임")
    }

    @Test
    @DisplayName("id가 있는 도메인 Member를 MemberEntity로 변환하면 id가 유지되어야 한다")
    fun `id가 있는 도메인 Member를 MemberEntity로 변환하면 id가 유지된다`() {
        // given
        val member = Member(id = 1L, email = "test@example.com", password = "hashed", nickname = "닉네임")

        // when
        val entity = MemberEntity.fromDomain(member)

        // then
        assertThat(entity.id).isEqualTo(1L)
        assertThat(entity.email).isEqualTo("test@example.com")
        assertThat(entity.password).isEqualTo("hashed")
        assertThat(entity.nickname).isEqualTo("닉네임")
    }

    @Test
    @DisplayName("MemberEntity를 도메인 Member로 변환할 수 있어야 한다")
    fun `MemberEntity를 도메인 Member로 변환할 수 있다`() {
        // given
        val entity = MemberEntity(id = 1L, email = "test@example.com", password = "hashed", nickname = "닉네임")

        // when
        val member = entity.toDomain()

        // then
        assertThat(member.id).isEqualTo(1L)
        assertThat(member.email).isEqualTo("test@example.com")
        assertThat(member.password).isEqualTo("hashed")
        assertThat(member.nickname).isEqualTo("닉네임")
    }

    @Test
    @DisplayName("id가 없는 MemberEntity를 도메인 Member로 변환하면 id가 null이어야 한다")
    fun `id가 없는 MemberEntity를 도메인 Member로 변환하면 id가 null이다`() {
        // given
        val entity = MemberEntity(email = "test@example.com", password = "hashed", nickname = "닉네임")

        // when
        val member = entity.toDomain()

        // then
        assertThat(member.id).isNull()
        assertThat(member.email).isEqualTo("test@example.com")
    }
}
