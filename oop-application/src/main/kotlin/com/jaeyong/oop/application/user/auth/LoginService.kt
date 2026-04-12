package com.jaeyong.oop.application.user.auth

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.port.JwtHandler
import com.jaeyong.oop.domain.user.port.PasswordEncryptor
import com.jaeyong.oop.domain.user.port.UserOutputPort
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val userRepository: UserOutputPort,
    private val passwordEncryptor: PasswordEncryptor,
    private val jwtProvider: JwtHandler
) : LoginUseCase {

    override fun login(username: String, password: String): String {
        val user = userRepository.getByUsername(username)
            ?: throw BaseException(ErrorCode.UNAUTHORIZED)

        if (!user.matchesPassword(password, passwordEncryptor)) {
            throw BaseException(ErrorCode.UNAUTHORIZED)
        }
        return jwtProvider.generateToken(username)
    }
}
