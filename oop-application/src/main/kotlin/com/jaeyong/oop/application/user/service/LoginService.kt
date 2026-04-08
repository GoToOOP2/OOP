package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.usecase.LoginUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.port.JwtProvider
import com.jaeyong.oop.domain.user.port.PasswordEncryptor
import com.jaeyong.oop.domain.user.port.UserRepository
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val userRepository: UserRepository,
    private val passwordEncryptor: PasswordEncryptor,
    private val jwtProvider: JwtProvider
) : LoginUseCase {

    override fun login(username: String, password: String): String {
        val user = userRepository.findByUsername(username)
            ?: throw BaseException(ErrorCode.UNAUTHORIZED)

        if (!passwordEncryptor.matches(password, user.password)) {
            throw BaseException(ErrorCode.UNAUTHORIZED)
        }

        return jwtProvider.generateToken(username)
    }
}
