package com.jaeyong.oop.domain.user

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.user.port.PasswordEncryptorPort
import com.jaeyong.oop.domain.user.port.UserPort
import com.jaeyong.oop.domain.user.vo.EncodedPasswordVO
import com.jaeyong.oop.domain.user.vo.RawPasswordVO
import com.jaeyong.oop.domain.user.vo.UsernameVO

data class User(
    val id: Long? = null,
    val username: UsernameVO,
    val password: EncodedPasswordVO
) {
    fun authenticate(rawPassword: RawPasswordVO, passwordEncryptor: PasswordEncryptorPort) {
        if (!passwordEncryptor.matches(rawPassword, password)) {
            throw BaseException(ErrorCode.UNAUTHORIZED)
        }
    }

    companion object {
        fun signUp(username: UsernameVO, password: RawPasswordVO, passwordEncryptor: PasswordEncryptorPort, userPort: UserPort): User {
            if (userPort.isUsernameTaken(username)) {
                throw BaseException(ErrorCode.DUPLICATE)
            }
            return User(username = username, password = passwordEncryptor.encrypt(password))
        }
    }
}
