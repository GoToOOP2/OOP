package com.jaeyong.oop.domain.user.port

interface JwtProvider {
    fun generateToken(username: String): String
    fun extractUsername(token: String): String
    fun isValid(token: String): Boolean
}
