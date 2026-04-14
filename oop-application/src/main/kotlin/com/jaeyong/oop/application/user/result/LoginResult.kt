package com.jaeyong.oop.application.user.result

/**
 * 로그인 유스케이스 결과.
 *
 * @property token 발급된 access token
 * @property refreshToken 발급된 refresh token
 */
data class LoginResult private constructor(
    val token: String,
    val refreshToken: String
) {
    companion object {
        fun of(token: String, refreshToken: String): LoginResult = LoginResult(token, refreshToken)
    }
}
