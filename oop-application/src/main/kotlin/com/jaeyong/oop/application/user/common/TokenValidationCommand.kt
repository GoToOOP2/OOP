package com.jaeyong.oop.application.user.common

data class TokenValidationCommand private constructor(
    val token: String
) {
    companion object {
        fun of(token: String): TokenValidationCommand = TokenValidationCommand(token)
    }
}
