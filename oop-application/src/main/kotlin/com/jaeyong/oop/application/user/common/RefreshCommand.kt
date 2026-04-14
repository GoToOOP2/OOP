package com.jaeyong.oop.application.user.common

/**
 * 토큰 갱신 유스케이스 입력 커맨드.
 *
 * @property refreshToken 갱신에 사용할 refresh token
 */
data class RefreshCommand private constructor(
    val refreshToken: String
) {
    companion object {
        fun of(refreshToken: String): RefreshCommand = RefreshCommand(refreshToken)
    }
}
