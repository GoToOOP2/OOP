package com.jaeyong.oop.domain.user.vo

/**
 * 암호화 전 평문 비밀번호 Value Object.
 *
 * [EncodedPasswordVO]와 타입을 분리해 암호화 전/후 비밀번호 혼용 실수를 컴파일 타임에 방지한다.
 */
data class RawPasswordVO private constructor(val value: String) {
    companion object {
        /**
         * @param value 평문 비밀번호 문자열
         */
        fun from(value: String): RawPasswordVO = RawPasswordVO(value)
    }
}
