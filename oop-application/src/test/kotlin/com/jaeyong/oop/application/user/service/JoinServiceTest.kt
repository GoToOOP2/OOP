package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.common.JoinCommand
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.port.PasswordEncryptorPort
import com.jaeyong.oop.domain.user.port.UserPort
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
    private lateinit var userPort: UserPort

    @Mock
    private lateinit var passwordEncryptorPort: PasswordEncryptorPort

    @InjectMocks
    private lateinit var sut: JoinService

    @Test
    @DisplayName("1. 이미 존재하는 username이면 DUPLICATE 예외를 던진다")
    fun `이미 존재하는 username이면 DUPLICATE 예외를 던진다`() {
        // given
        given(userPort.isUsernameTaken(anyString())).willReturn(true)

        // when & then
        val exception = assertThrows<BaseException> {
            sut.join(JoinCommand(username = "jaeyong", password = "password123"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.DUPLICATE)
    }

}
