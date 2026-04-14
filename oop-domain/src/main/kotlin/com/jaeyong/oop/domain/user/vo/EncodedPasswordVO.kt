package com.jaeyong.oop.domain.user.vo

/**
 * 암호화된 비밀번호 Value Object.
 *
 * [RawPasswordVO]와 타입을 분리해 암호화 전/후 비밀번호 혼용 실수를 컴파일 타임에 방지한다.
 */
data class EncodedPasswordVO private constructor(val value: String) {
    companion object {
        /**
         * @param value 암호화된 비밀번호 문자열
         */
        fun from(value: String): EncodedPasswordVO = EncodedPasswordVO(value)
    }
}
