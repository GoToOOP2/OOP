package com.jaeyong.oop.domain.user.port

interface PasswordEncryptor {
    fun encrypt(raw: String): String
}
