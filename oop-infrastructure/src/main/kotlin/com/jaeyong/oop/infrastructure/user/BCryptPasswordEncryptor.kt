package com.jaeyong.oop.infrastructure.user

import com.jaeyong.oop.domain.user.port.PasswordEncryptor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncryptor : PasswordEncryptor {
    private val encoder = BCryptPasswordEncoder()

    override fun encrypt(raw: String): String = encoder.encode(raw)
}
