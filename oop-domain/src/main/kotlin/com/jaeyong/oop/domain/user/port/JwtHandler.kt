package com.jaeyong.oop.domain.user.port

interface JwtHandler {
    fun generateToken(username: String): String
    fun extractUsername(token: String): String
    fun isValid(token: String): Boolean
}
