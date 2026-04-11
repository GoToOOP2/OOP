package com.jaeyong.oop.infrastructure.user

import com.jaeyong.oop.domain.user.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserPersistenceAdapterTest {

    @Mock
    private lateinit var userJpaRepository: UserJpaRepository

    @InjectMocks
    private lateinit var sut: UserPersistenceAdapter

    @Test
    @DisplayName("1. register 호출 시 저장된 도메인 객체를 반환한다")
    fun `register 호출 시 저장된 도메인 객체를 반환한다`() {
        // given
        val user = User(username = "jaeyong", password = "hashed")
        val savedEntity = UserEntity(id = 1L, username = "jaeyong", password = "hashed")
        given(userJpaRepository.save(any(UserEntity::class.java))).willReturn(savedEntity)

        // when
        val result = sut.register(user)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.username).isEqualTo("jaeyong")
    }

    @Test
    @DisplayName("2. isUsernameTaken - 존재하는 username이면 true를 반환한다")
    fun `isUsernameTaken - 존재하는 username이면 true를 반환한다`() {
        // given
        given(userJpaRepository.existsByUsername("jaeyong")).willReturn(true)

        // when & then
        assertThat(sut.isUsernameTaken("jaeyong")).isTrue()
    }

    @Test
    @DisplayName("3. isUsernameTaken - 존재하지 않는 username이면 false를 반환한다")
    fun `isUsernameTaken - 존재하지 않는 username이면 false를 반환한다`() {
        // given
        given(userJpaRepository.existsByUsername("unknown")).willReturn(false)

        // when & then
        assertThat(sut.isUsernameTaken("unknown")).isFalse()
    }

    @Test
    @DisplayName("4. getByUsername - 존재하는 username이면 도메인 객체를 반환한다")
    fun `getByUsername - 존재하는 username이면 도메인 객체를 반환한다`() {
        // given
        val entity = UserEntity(id = 1L, username = "jaeyong", password = "hashed")
        given(userJpaRepository.findByUsername("jaeyong")).willReturn(entity)

        // when
        val result = sut.getByUsername("jaeyong")

        // then
        assertThat(result).isNotNull
        assertThat(result?.username).isEqualTo("jaeyong")
    }

    @Test
    @DisplayName("5. getByUsername - 존재하지 않는 username이면 null을 반환한다")
    fun `getByUsername - 존재하지 않는 username이면 null을 반환한다`() {
        // given
        given(userJpaRepository.findByUsername("unknown")).willReturn(null)

        // when
        val result = sut.getByUsername("unknown")

        // then
        assertThat(result).isNull()
    }
}
