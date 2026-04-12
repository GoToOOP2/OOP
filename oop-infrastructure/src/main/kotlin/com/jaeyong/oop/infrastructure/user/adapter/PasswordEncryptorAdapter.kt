package com.jaeyong.oop.infrastructure.user.adapter

import com.jaeyong.oop.domain.user.port.PasswordEncryptorPort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncryptorAdapter : PasswordEncryptorPort {
    private val encoder = BCryptPasswordEncoder()

    override fun encrypt(raw: String): String = encoder.encode(raw)

    override fun matches(raw: String, encoded: String): Boolean = encoder.matches(raw, encoded)
}
