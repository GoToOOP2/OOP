package com.jaeyong.oop.infrastructure.security

import at.favre.lib.crypto.bcrypt.BCrypt
import com.jaeyong.oop.domain.port.PasswordEncodePort
import org.springframework.stereotype.Component

/**
 * PasswordEncodePort의 구현체 (아웃바운드 어댑터).
 * BCrypt 알고리즘으로 비밀번호를 해싱하고 검증한다.
 * cost factor 12: 해싱에 약 250ms 소요 — 보안성과 성능의 균형점.
 *
 * 비밀번호뿐 아니라 Refresh Token 해싱에도 사용된다.
 */
@Component
class BcryptAdapter : PasswordEncodePort {

    /** 평문을 BCrypt로 해싱한다. 매 호출마다 랜덤 salt가 생성되어 같은 입력도 다른 해시값을 반환한다. */
    override fun encode(rawPassword: String): String {
        return BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray())
    }

    /** 평문이 해싱된 값과 일치하는지 검증한다. */
    override fun matches(rawPassword: String, encodedPassword: String, ): Boolean {
        return BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword).verified
    }
}
