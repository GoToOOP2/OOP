package com.jaeyong.oop.application.user.common

/**
 * 로그인 유스케이스 입력 커맨드.
 *
 * @property username 로그인할 사용자명
 * @property password 로그인할 평문 비밀번호
 */
data class LoginCommand private constructor(
    val username: String,
    val password: String
) {
    companion object {
        fun of(username: String, password: String): LoginCommand = LoginCommand(username, password)
    }
}
