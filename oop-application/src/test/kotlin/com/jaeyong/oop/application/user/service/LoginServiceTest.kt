package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.common.LoginCommand
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.vo.EncodedPasswordVO
import com.jaeyong.oop.domain.user.vo.RawPasswordVO
import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.vo.UsernameVO
import com.jaeyong.oop.domain.user.port.JwtHandlerPort
import com.jaeyong.oop.domain.user.port.PasswordEncryptorPort
import com.jaeyong.oop.domain.user.port.UserPort
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
    private lateinit var userPort: UserPort

    @Mock
    private lateinit var passwordEncryptorPort: PasswordEncryptorPort

    @Mock
    private lateinit var jwtHandlerPort: JwtHandlerPort

    @InjectMocks
    private lateinit var sut: LoginService

    @Test
    @DisplayName("1. 존재하지 않는 username이면 UNAUTHORIZED 예외를 던진다")
    fun `존재하지 않는 username이면 UNAUTHORIZED 예외를 던진다`() {
        // given
        given(userPort.getByUsername(UsernameVO.from("unknown"))).willReturn(null)

        // when & then
        val exception = assertThrows<BaseException> {
            sut.login(LoginCommand.of(username = "unknown", password = "password123"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.UNAUTHORIZED)
    }

    @Test
    @DisplayName("2. 비밀번호가 일치하지 않으면 UNAUTHORIZED 예외를 던진다")
    fun `비밀번호가 일치하지 않으면 UNAUTHORIZED 예외를 던진다`() {
        // given
        val user = User(id = 1L, username = UsernameVO.from("jaeyong"), password = EncodedPasswordVO.from("hashed_password"))
        given(userPort.getByUsername(UsernameVO.from("jaeyong"))).willReturn(user)
        given(passwordEncryptorPort.matches(RawPasswordVO.from("wrongpassword"), EncodedPasswordVO.from("hashed_password"))).willReturn(false)

        // when & then
        val exception = assertThrows<BaseException> {
            sut.login(LoginCommand.of(username = "jaeyong", password = "wrongpassword"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.UNAUTHORIZED)
    }

    @Test
    @DisplayName("3. 정상 로그인 시 JWT 토큰을 반환한다")
    fun `정상 로그인 시 JWT 토큰을 반환한다`() {
        // given
        val user = User(id = 1L, username = UsernameVO.from("jaeyong"), password = EncodedPasswordVO.from("hashed_password"))
        given(userPort.getByUsername(UsernameVO.from("jaeyong"))).willReturn(user)
        given(passwordEncryptorPort.matches(RawPasswordVO.from("password123"), EncodedPasswordVO.from("hashed_password"))).willReturn(true)
        given(jwtHandlerPort.generateToken(UsernameVO.from("jaeyong"))).willReturn(TokenVO.from("jwt.token.string"))

        // when
        val result = sut.login(LoginCommand.of(username = "jaeyong", password = "password123"))

        // then
        assertThat(result.token).isEqualTo("jwt.token.string")
    }
}
