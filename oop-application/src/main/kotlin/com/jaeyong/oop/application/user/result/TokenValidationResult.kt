package com.jaeyong.oop.application.user.result

data class TokenValidationResult private constructor(val username: String?) {
    companion object {
        fun of(username: String?): TokenValidationResult = TokenValidationResult(username)
    }
}
