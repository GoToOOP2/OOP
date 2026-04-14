package com.jaeyong.oop.domain.user.vo

/**
 * JWT 토큰 Value Object.
 *
 * 일반 String과 타입을 분리해 토큰 문자열 혼용 실수를 컴파일 타임에 방지한다.
 */
data class TokenVO private constructor(val value: String) {
    companion object {
        /**
         * @param value JWT 토큰 문자열
         */
        fun from(value: String): TokenVO = TokenVO(value)
    }
}
