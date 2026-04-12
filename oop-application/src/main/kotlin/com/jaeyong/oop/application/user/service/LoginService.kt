package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.common.LoginCommand
import com.jaeyong.oop.application.user.result.LoginResult
import com.jaeyong.oop.application.user.usecase.LoginUseCase
import com.jaeyong.oop.domain.user.User
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
        val user = User.login(
            username = UsernameVO(command.username),
            password = RawPasswordVO(command.password),
            userPort = userPort,
            passwordEncryptor = passwordEncryptorPort
        )
        val token = jwtHandlerPort.generateToken(user.username)
        return LoginResult(token = token.value)
    }
}
