package com.jaeyong.oop.application.user.common

import kotlin.ConsistentCopyVisibility

/**
 * 로그인 유스케이스 입력 커맨드.
 *
 * @property username 로그인할 사용자명
 * @property password 로그인할 평문 비밀번호
 */
@ConsistentCopyVisibility
data class LoginCommand private constructor(
    val username: String,
    val password: String
) {
    companion object {
        fun of(username: String, password: String): LoginCommand = LoginCommand(username, password)
    }
}
