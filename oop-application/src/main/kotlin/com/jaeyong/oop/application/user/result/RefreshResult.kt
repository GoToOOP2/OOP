package com.jaeyong.oop.application.user.result

/**
 * 토큰 갱신 유스케이스 결과.
 *
 * @property accessToken 새로 발급된 access token
 * @property refreshToken 새로 발급된 refresh token
 */
data class RefreshResult private constructor(
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun of(accessToken: String, refreshToken: String): RefreshResult = RefreshResult(accessToken, refreshToken)
    }
}
