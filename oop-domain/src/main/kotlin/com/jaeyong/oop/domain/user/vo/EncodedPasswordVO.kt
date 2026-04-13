package com.jaeyong.oop.domain.user.vo

// 암호화된 비밀번호. RawPasswordVO와 타입을 분리해 혼용 실수를 컴파일 타임에 방지
data class EncodedPasswordVO private constructor(val value: String) {
    companion object {
        fun from(value: String): EncodedPasswordVO = EncodedPasswordVO(value)
    }
}
