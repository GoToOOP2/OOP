package com.jaeyong.oop.application.user.common

data class JoinCommand private constructor(
    val username: String,
    val password: String
) {
    companion object {
        fun of(username: String, password: String): JoinCommand = JoinCommand(username, password)
    }
}
