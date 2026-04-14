package com.jaeyong.oop.application.user.result

/**
 * 토큰 검증 유스케이스 결과.
 *
 * @property username 유효한 토큰이면 사용자명, 유효하지 않으면 null
 */
data class TokenValidationResult private constructor(val username: String?) {
    companion object {
        fun of(username: String?): TokenValidationResult = TokenValidationResult(username)
    }
}
