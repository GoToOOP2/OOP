package com.jaeyong.oop.domain.user.port

interface JwtHandler {
    fun generateToken(username: String): String
    fun validateAndExtract(token: String): String?
}
