package com.jaeyong.oop.application.user.result

data class LoginResult private constructor(val token: String) {
    companion object {
        fun of(token: String): LoginResult = LoginResult(token)
    }
}
