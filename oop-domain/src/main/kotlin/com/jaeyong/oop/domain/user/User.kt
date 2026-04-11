package com.jaeyong.oop.domain.user

import com.jaeyong.oop.domain.user.port.PasswordEncryptor

data class User(
    val id: Long? = null,
    val username: String,
    val password: String
) {
    fun matchesPassword(rawPassword: String, passwordEncryptor: PasswordEncryptor): Boolean =
        passwordEncryptor.matches(rawPassword, password)

    companion object {
        fun signUp(username: String, password: String, passwordEncryptor: PasswordEncryptor): User =
            User(username = username, password = passwordEncryptor.encrypt(password))
    }
}
