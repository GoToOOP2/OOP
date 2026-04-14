package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.vo.EncodedPasswordVO
import com.jaeyong.oop.domain.user.vo.RawPasswordVO

interface PasswordEncryptorPort {

    /**
     * 평문 비밀번호를 암호화한다.
     *
     * @param raw 암호화할 평문 비밀번호
     * @return 암호화된 비밀번호
     */
    fun encrypt(raw: RawPasswordVO): EncodedPasswordVO

    /**
     * 평문 비밀번호와 암호화된 비밀번호가 일치하는지 검증한다.
     *
     * @param raw 검증할 평문 비밀번호
     * @param encoded 비교 대상 암호화된 비밀번호
     * @return 일치하면 true
     */
    fun matches(raw: RawPasswordVO, encoded: EncodedPasswordVO): Boolean
}
