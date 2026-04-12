package com.jaeyong.oop.application.user.service

import com.jaeyong.oop.application.user.usecase.JoinUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.application.user.common.JoinCommand
import com.jaeyong.oop.domain.user.port.PasswordEncryptorPort
import com.jaeyong.oop.domain.user.port.UserPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JoinService(
    private val userPort: UserPort,
    private val passwordEncryptorPort: PasswordEncryptorPort
) : JoinUseCase {

    @Transactional
    override fun join(command: JoinCommand) {
        if (userPort.isUsernameTaken(command.username)) {
            throw BaseException(ErrorCode.DUPLICATE)
        }
        val user = User.signUp(username = command.username, password = command.password, passwordEncryptor = passwordEncryptorPort)
        userPort.register(user)
    }
}
