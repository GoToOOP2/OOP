package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.usecase.LoginUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
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

    override fun login(username: String, password: String): String {
        val user = userPort.getByUsername(username)
            ?: throw BaseException(ErrorCode.UNAUTHORIZED)

        if (!user.matchesPassword(password, passwordEncryptorPort)) {
            throw BaseException(ErrorCode.UNAUTHORIZED)
        }
        return jwtHandlerPort.generateToken(username)
    }
}
