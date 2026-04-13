package com.jaeyong.oop.domain.user.vo

// 암호화 전 평문 비밀번호를 타입으로 구분
// String만 쓰면 암호화 전/후 비밀번호가 혼용될 위험이 있어서 VO로 분리
data class RawPasswordVO private constructor(val value: String) {
    companion object {
        fun from(value: String): RawPasswordVO = RawPasswordVO(value)
    }
}
