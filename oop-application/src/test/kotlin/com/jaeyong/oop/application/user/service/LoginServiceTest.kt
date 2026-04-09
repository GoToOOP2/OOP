package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.port.JwtProvider
import com.jaeyong.oop.domain.user.port.PasswordEncryptor
import com.jaeyong.oop.domain.user.port.UserOutputPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class LoginServiceTest {

    @Mock
    private lateinit var userRepository: UserOutputPort

    @Mock
    private lateinit var passwordEncryptor: PasswordEncryptor

    @Mock
    private lateinit var jwtProvider: JwtProvider

    @InjectMocks
    private lateinit var sut: LoginService

    @Test
    @DisplayName("1. 존재하지 않는 username이면 UNAUTHORIZED 예외를 던진다")
    fun `존재하지 않는 username이면 UNAUTHORIZED 예외를 던진다`() {
        // given
        given(userRepository.findByUsername("unknown")).willReturn(null)

        // when & then
        val exception = assertThrows<BaseException> {
            sut.login("unknown", "password123")
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.UNAUTHORIZED)
    }

    @Test
    @DisplayName("2. 비밀번호가 일치하지 않으면 UNAUTHORIZED 예외를 던진다")
    fun `비밀번호가 일치하지 않으면 UNAUTHORIZED 예외를 던진다`() {
        // given
        val user = User(id = 1L, username = "jaeyong", password = "hashed_password")
        given(userRepository.findByUsername("jaeyong")).willReturn(user)
        given(passwordEncryptor.matches("wrongpassword", "hashed_password")).willReturn(false)

        // when & then
        val exception = assertThrows<BaseException> {
            sut.login("jaeyong", "wrongpassword")
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.UNAUTHORIZED)
    }

    @Test
    @DisplayName("3. 정상 로그인 시 JWT 토큰을 반환한다")
    fun `정상 로그인 시 JWT 토큰을 반환한다`() {
        // given
        val user = User(id = 1L, username = "jaeyong", password = "hashed_password")
        given(userRepository.findByUsername("jaeyong")).willReturn(user)
        given(passwordEncryptor.matches("password123", "hashed_password")).willReturn(true)
        given(jwtProvider.generateToken("jaeyong")).willReturn("jwt.token.string")

        // when
        val token = sut.login("jaeyong", "password123")

        // then
        assertThat(token).isEqualTo("jwt.token.string")
    }
}
