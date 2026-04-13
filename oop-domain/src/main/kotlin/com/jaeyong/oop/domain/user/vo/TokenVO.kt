package com.jaeyong.oop.domain.user.vo

// JWT 토큰 문자열을 String과 구분하기 위한 VO
// 토큰임을 타입으로 명시해 일반 String과 혼용 실수 방지
data class TokenVO private constructor(val value: String) {
    companion object {
        fun from(value: String): TokenVO = TokenVO(value)
    }
}
