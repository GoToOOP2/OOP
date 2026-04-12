package com.jaeyong.oop.domain.user

import com.jaeyong.oop.domain.user.port.PasswordEncryptorPort
import com.jaeyong.oop.domain.user.vo.EncodedPasswordVO
import com.jaeyong.oop.domain.user.vo.RawPasswordVO
import com.jaeyong.oop.domain.user.vo.UsernameVO

data class User(
    val id: Long? = null,
    val username: UsernameVO,
    val password: EncodedPasswordVO
) {
    fun matchesPassword(rawPassword: RawPasswordVO, passwordEncryptor: PasswordEncryptorPort): Boolean =
        passwordEncryptor.matches(rawPassword, password)

    companion object {
        fun signUp(username: UsernameVO, password: RawPasswordVO, passwordEncryptor: PasswordEncryptorPort): User =
            User(username = username, password = passwordEncryptor.encrypt(password))
    }
}
