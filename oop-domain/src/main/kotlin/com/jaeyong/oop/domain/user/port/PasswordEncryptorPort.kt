package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.vo.EncodedPasswordVO
import com.jaeyong.oop.domain.user.vo.RawPasswordVO

interface PasswordEncryptorPort {
    fun encrypt(raw: RawPasswordVO): EncodedPasswordVO
    fun matches(raw: RawPasswordVO, encoded: EncodedPasswordVO): Boolean
}
