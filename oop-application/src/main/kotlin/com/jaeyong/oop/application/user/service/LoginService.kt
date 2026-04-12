package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.common.LoginCommand
import com.jaeyong.oop.application.user.result.LoginResult
import com.jaeyong.oop.application.user.usecase.LoginUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.vo.RawPasswordVO
import com.jaeyong.oop.domain.user.vo.UsernameVO
import com.jaeyong.oop.domain.user.port.JwtHandlerPort
import com.jaeyong.oop.domain.user.port.PasswordEncryptorPort
import com.jaeyong.oop.domain.user.port.UserPort
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val userPort: UserPort,
    private val passwordEncryptorPort: PasswordEncryptorPort,
    private val jwtHandlerPort: JwtHandlerPort
) : LoginUseCase {

    override fun login(command: LoginCommand): LoginResult {
        val username = UsernameVO(command.username)
        val rawPassword = RawPasswordVO(command.password)

        val user = userPort.getByUsername(username)
            ?: throw BaseException(ErrorCode.UNAUTHORIZED)

        if (!user.matchesPassword(rawPassword, passwordEncryptorPort)) {
            throw BaseException(ErrorCode.UNAUTHORIZED)
        }
        val token = jwtHandlerPort.generateToken(username)
        return LoginResult(token = token.value)
    }
}
