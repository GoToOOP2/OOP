package com.jaeyong.oop.infrastructure.user.entity

import com.jaeyong.oop.domain.user.vo.EncodedPasswordVO
import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.vo.UsernameVO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserEntityTest {

    @Test
    @DisplayName("1. 생성자로 필드가 정상 초기화된다")
    fun `생성자로 필드가 정상 초기화된다`() {
        // when
        val entity = UserEntity(id = 1L, username = "jaeyong", password = "hashed")

        // then
        assertThat(entity.id).isEqualTo(1L)
        assertThat(entity.username).isEqualTo("jaeyong")
        assertThat(entity.password).isEqualTo("hashed")
    }

    @Test
    @DisplayName("2. id 기본값 생성자 — id가 null로 초기화된다")
    fun `id 기본값 생성자 - id가 null로 초기화된다`() {
        // when — id 생략 시 기본값 null 사용 (DefaultConstructorMarker 커버)
        val entity = UserEntity(username = "jaeyong", password = "hashed")

        // then
        assertThat(entity.id).isNull()
        assertThat(entity.username).isEqualTo("jaeyong")
    }

    @Test
    @DisplayName("3. toDomain() 호출 시 도메인 객체로 정확히 변환된다")
    fun `toDomain 호출 시 도메인 객체로 정확히 변환된다`() {
        // given
        val entity = UserEntity(id = 1L, username = "jaeyong", password = "hashed")

        // when
        val domain = entity.toDomain()

        // then
        assertThat(domain.id).isEqualTo(1L)
        assertThat(domain.username).isEqualTo(UsernameVO.from("jaeyong"))
        assertThat(domain.password).isEqualTo(EncodedPasswordVO.from("hashed"))
    }

    @Test
    @DisplayName("4. fromDomain() 호출 시 엔티티로 정확히 변환된다")
    fun `fromDomain 호출 시 엔티티로 정확히 변환된다`() {
        // given
        val domain = User.reconstruct(1L, UsernameVO.from("jaeyong"), EncodedPasswordVO.from("hashed"))

        // when
        val entity = UserEntity.fromDomain(domain)

        // then
        assertThat(entity.id).isEqualTo(1L)
        assertThat(entity.username).isEqualTo("jaeyong")
        assertThat(entity.password).isEqualTo("hashed")
    }
}
