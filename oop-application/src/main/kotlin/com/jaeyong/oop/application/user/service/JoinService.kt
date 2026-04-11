package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.usecase.JoinUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.port.PasswordEncryptor
import com.jaeyong.oop.domain.user.port.UserOutputPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JoinService(
    private val userRepository: UserOutputPort,
    private val passwordEncryptor: PasswordEncryptor
) : JoinUseCase {

    @Transactional
    override fun join(command: JoinCommand) {
        if (userRepository.isUsernameTaken(command.username)) {
            throw BaseException(ErrorCode.DUPLICATE)
        }
        val user = User.signUp(username = command.username, password = command.password, passwordEncryptor = passwordEncryptor)
        userRepository.register(user)
    }
}
