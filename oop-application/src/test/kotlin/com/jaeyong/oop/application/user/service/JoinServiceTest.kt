package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.port.PasswordEncryptor
import com.jaeyong.oop.domain.user.port.UserOutputPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor

@ExtendWith(MockitoExtension::class)
class JoinServiceTest {

    @Mock
    private lateinit var userRepository: UserOutputPort

    @Mock
    private lateinit var passwordEncryptor: PasswordEncryptor

    @InjectMocks
    private lateinit var sut: JoinService

    @Test
    @DisplayName("1. 이미 존재하는 username이면 DUPLICATE 예외를 던진다")
    fun `이미 존재하는 username이면 DUPLICATE 예외를 던진다`() {
        // given
        given(userRepository.isUsernameTaken(anyString())).willReturn(true)

        // when & then
        val exception = assertThrows<BaseException> {
            sut.join(JoinCommand(username = "jaeyong", password = "password123"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.DUPLICATE)
    }

    @Test
    @DisplayName("2. 정상 가입 시 비밀번호가 암호화되어 저장된다")
    fun `정상 가입 시 비밀번호가 암호화되어 저장된다`() {
        // given
        given(userRepository.isUsernameTaken(anyString())).willReturn(false)
        given(passwordEncryptor.encrypt("password123")).willReturn("hashed_password")
        given(userRepository.register(any())).willReturn(User(id = 1L, username = "jaeyong", password = "hashed_password"))

        // when
        sut.join(JoinCommand(username = "jaeyong", password = "password123"))

        // then
        val captor = argumentCaptor<User>()
        org.mockito.BDDMockito.verify(userRepository).save(captor.capture())
        assertThat(captor.firstValue.password).isEqualTo("hashed_password")
    }
}
