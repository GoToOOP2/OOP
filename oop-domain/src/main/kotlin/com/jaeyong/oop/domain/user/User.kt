package com.jaeyong.oop.domain.user

import com.jaeyong.oop.domain.user.port.PasswordEncryptorPort

data class User(
    val id: Long? = null,
    val username: String,
    val password: String
) {
    fun matchesPassword(rawPassword: String, passwordEncryptor: PasswordEncryptorPort): Boolean =
        passwordEncryptor.matches(rawPassword, password)

    companion object {
        fun signUp(username: String, password: String, passwordEncryptor: PasswordEncryptorPort): User =
            User(username = username, password = passwordEncryptor.encrypt(password))
    }
}
