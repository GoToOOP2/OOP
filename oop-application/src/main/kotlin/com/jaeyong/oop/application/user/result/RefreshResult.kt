package com.jaeyong.oop.application.user.result

data class RefreshResult private constructor(
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun of(accessToken: String, refreshToken: String): RefreshResult = RefreshResult(accessToken, refreshToken)
    }
}
