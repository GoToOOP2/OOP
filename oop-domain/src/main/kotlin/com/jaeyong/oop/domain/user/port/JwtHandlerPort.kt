package com.jaeyong.oop.domain.user.port

interface JwtHandlerPort {
    fun generateToken(username: String): String
    fun validateAndExtract(token: String): String?
}
