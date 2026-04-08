package com.jaeyong.oop.application.service

import com.jaeyong.oop.domain.port.JwtPort
import com.jaeyong.oop.domain.port.PasswordEncodePort
import com.jaeyong.oop.domain.port.RefreshTokenPort
import com.jaeyong.oop.application.command.LoginCommand
import com.jaeyong.oop.application.command.SignupCommand
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.member.Member
import com.jaeyong.oop.domain.member.port.MemberPort
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {
    @Mock
    private lateinit var memberPort: MemberPort

    @Mock
    private lateinit var jwtPort: JwtPort

    @Mock
    private lateinit var passwordEncodePort: PasswordEncodePort

    @Mock
    private lateinit var refreshTokenPort: RefreshTokenPort

    @InjectMocks
    private lateinit var authService: AuthService

    private fun <T> anyNonNull(): T {
        ArgumentMatchers.any<T>()
        @Suppress("UNCHECKED_CAST")
        return null as T
    }

    @Test
    @DisplayName("회원가입 성공 시 비밀번호를 해싱하여 저장해야 한다")
    fun `회원가입 성공 시 비밀번호를 해싱하여 저장한다`() {
        // given
        val command = SignupCommand("test@example.com", "password1", "닉네임")
        `when`(memberPort.existsByEmail("test@example.com")).thenReturn(false)
        `when`(passwordEncodePort.encode("password1")).thenReturn("hashedPassword")
        `when`(memberPort.save(anyNonNull())).thenReturn(
            Member(id = 1L, email = "test@example.com", password = "hashedPassword", nickname = "닉네임"),
        )

        // when
        authService.signup(command)

        // then
        verify(passwordEncodePort).encode("password1")
        verify(memberPort).save(anyNonNull())
    }

    @Test
    @DisplayName("이미 가입된 이메일로 회원가입 시 예외가 발생해야 한다")
    fun `중복된 이메일로 회원가입 시 예외가 발생한다`() {
        // given
        val command = SignupCommand("test@example.com", "password1", "닉네임")
        `when`(memberPort.existsByEmail("test@example.com")).thenReturn(true)

        // when & then
        assertThatThrownBy { authService.signup(command) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.DUPLICATE_EMAIL)
    }

    @Test
    @DisplayName("로그인 성공 시 Access Token과 Refresh Token을 반환해야 한다")
    fun `로그인 성공 시 토큰을 반환한다`() {
        // given
        val command = LoginCommand("test@example.com", "password1")
        val member = Member(id = 1L, email = "test@example.com", password = "hashedPassword", nickname = "닉네임")
        `when`(memberPort.findByEmail("test@example.com")).thenReturn(member)
        `when`(passwordEncodePort.matches("password1", "hashedPassword")).thenReturn(true)
        `when`(jwtPort.createAccessToken(1L)).thenReturn("access-token")
        `when`(jwtPort.createRefreshToken(1L)).thenReturn("refresh-token")
        `when`(passwordEncodePort.encode("refresh-token")).thenReturn("hashed-refresh-token")

        // when
        val result = authService.login(command)

        // then
        assertThat(result.accessToken).isEqualTo("access-token")
        assertThat(result.refreshToken).isEqualTo("refresh-token")
        verify(refreshTokenPort).save(1L, "hashed-refresh-token")
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생해야 한다")
    fun `잘못된 비밀번호로 로그인 시 예외가 발생한다`() {
        // given
        val command = LoginCommand("test@example.com", "wrongPassword")
        val member = Member(id = 1L, email = "test@example.com", password = "hashedPassword", nickname = "닉네임")
        `when`(memberPort.findByEmail("test@example.com")).thenReturn(member)
        `when`(passwordEncodePort.matches("wrongPassword", "hashedPassword")).thenReturn(false)

        // when & then
        assertThatThrownBy { authService.login(command) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.LOGIN_FAILED)
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생해야 한다")
    fun `존재하지 않는 이메일로 로그인 시 예외가 발생한다`() {
        // given
        val command = LoginCommand("notfound@example.com", "password1")
        `when`(memberPort.findByEmail("notfound@example.com")).thenReturn(null)

        // when & then
        assertThatThrownBy { authService.login(command) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.LOGIN_FAILED)
    }

    @Test
    @DisplayName("토큰 재발급 성공 시 새 Access Token과 Refresh Token을 반환해야 한다")
    fun `토큰 재발급 성공 시 새 토큰을 반환한다`() {
        // given
        val refreshToken = "valid-refresh-token"
        `when`(jwtPort.extractMemberId(refreshToken)).thenReturn(1L)
        `when`(refreshTokenPort.findByMemberId(1L)).thenReturn("hashed-refresh-token")
        `when`(passwordEncodePort.matches(refreshToken, "hashed-refresh-token")).thenReturn(true)
        `when`(jwtPort.createAccessToken(1L)).thenReturn("new-access-token")
        `when`(jwtPort.createRefreshToken(1L)).thenReturn("new-refresh-token")
        `when`(passwordEncodePort.encode("new-refresh-token")).thenReturn("new-hashed-refresh-token")

        // when
        val result = authService.reissue(refreshToken)

        // then
        assertThat(result.accessToken).isEqualTo("new-access-token")
        assertThat(result.refreshToken).isEqualTo("new-refresh-token")
        verify(refreshTokenPort).save(1L, "new-hashed-refresh-token")
    }

    @Test
    @DisplayName("로그아웃 시 Refresh Token이 삭제되어야 한다")
    fun `로그아웃 시 리프레시 토큰이 삭제된다`() {
        // given
        val memberId = 1L

        // when
        authService.logout(memberId)

        // then
        verify(refreshTokenPort).deleteByMemberId(memberId)
    }

    // ── reissue 실패 케이스 ──

    @Test
    @DisplayName("만료된 Refresh Token으로 재발급 시 예외가 발생해야 한다")
    fun `만료된 리프레시 토큰으로 재발급 시 예외가 발생한다`() {
        // given
        val expiredToken = "expired-refresh-token"
        `when`(jwtPort.validateToken(expiredToken)).thenThrow(BaseException(ErrorCode.EXPIRED_TOKEN))

        // when & then
        assertThatThrownBy { authService.reissue(expiredToken) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.EXPIRED_TOKEN)
    }

    @Test
    @DisplayName("DB에 저장된 Refresh Token이 없으면 예외가 발생해야 한다")
    fun `저장된 리프레시 토큰이 없으면 예외가 발생한다`() {
        // given
        val refreshToken = "valid-refresh-token"
        `when`(jwtPort.extractMemberId(refreshToken)).thenReturn(1L)
        `when`(refreshTokenPort.findByMemberId(1L)).thenReturn(null)

        // when & then
        assertThatThrownBy { authService.reissue(refreshToken) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.INVALID_TOKEN)
    }

    @Test
    @DisplayName("Refresh Token이 DB의 해시와 불일치하면 예외가 발생해야 한다")
    fun `리프레시 토큰이 DB 해시와 불일치하면 예외가 발생한다`() {
        // given
        val refreshToken = "stolen-refresh-token"
        `when`(jwtPort.extractMemberId(refreshToken)).thenReturn(1L)
        `when`(refreshTokenPort.findByMemberId(1L)).thenReturn("hashed-refresh-token")
        `when`(passwordEncodePort.matches(refreshToken, "hashed-refresh-token")).thenReturn(false)

        // when & then
        assertThatThrownBy { authService.reissue(refreshToken) }
            .isInstanceOf(BaseException::class.java)
            .extracting("errorCode").isEqualTo(ErrorCode.INVALID_TOKEN)
    }

    // ── TokenValidationUseCase 위임 테스트 ──

    @Test
    @DisplayName("validateToken은 jwtPort에 위임해야 한다")
    fun `validateToken은 jwtPort에 위임한다`() {
        // given
        val token = "some-token"

        // when
        authService.validateToken(token)

        // then
        verify(jwtPort).validateToken(token)
    }

    @Test
    @DisplayName("extractMemberId는 jwtPort에 위임하여 memberId를 반환해야 한다")
    fun `extractMemberId는 jwtPort에 위임하여 memberId를 반환한다`() {
        // given
        val token = "some-token"
        `when`(jwtPort.extractMemberId(token)).thenReturn(1L)

        // when
        val memberId = authService.extractMemberId(token)

        // then
        assertThat(memberId).isEqualTo(1L)
        verify(jwtPort).extractMemberId(token)
    }
}
