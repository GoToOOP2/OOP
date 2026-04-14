package com.jaeyong.oop.infrastructure.user.adapter

import com.jaeyong.oop.domain.user.vo.EncodedPasswordVO
import com.jaeyong.oop.domain.user.vo.RawPasswordVO
import com.jaeyong.oop.domain.user.port.PasswordEncryptorPort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

/**
 * [PasswordEncryptorPort] Adapter — BCrypt를 사용해 비밀번호를 암호화하고 검증한다.
 */
@Component
class PasswordEncryptorAdapter : PasswordEncryptorPort {
    private val encoder = BCryptPasswordEncoder()

    override fun encrypt(raw: RawPasswordVO): EncodedPasswordVO = EncodedPasswordVO.from(encoder.encode(raw.value))

    override fun matches(raw: RawPasswordVO, encoded: EncodedPasswordVO): Boolean = encoder.matches(raw.value, encoded.value)
}
