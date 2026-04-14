package com.jaeyong.oop.application.user.common

data class RefreshCommand private constructor(
    val refreshToken: String
) {
    companion object {
        fun of(refreshToken: String): RefreshCommand = RefreshCommand(refreshToken)
    }
}
