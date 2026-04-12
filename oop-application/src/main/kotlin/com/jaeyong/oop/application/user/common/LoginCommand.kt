package com.jaeyong.oop.application.user.common

data class LoginCommand private constructor(
    val username: String,
    val password: String
) {
    companion object {
        fun of(username: String, password: String): LoginCommand = LoginCommand(username, password)
    }
}
