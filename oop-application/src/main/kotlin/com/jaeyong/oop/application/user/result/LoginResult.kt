package com.jaeyong.oop.application.user.result

data class LoginResult private constructor(
    val token: String,
    val refreshToken: String
) {
    companion object {
        fun of(token: String, refreshToken: String): LoginResult = LoginResult(token, refreshToken)
    }
}
