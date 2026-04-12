package com.jaeyong.oop.domain.user.port

interface PasswordEncryptorPort {
    fun encrypt(raw: String): String
    fun matches(raw: String, encoded: String): Boolean
}
