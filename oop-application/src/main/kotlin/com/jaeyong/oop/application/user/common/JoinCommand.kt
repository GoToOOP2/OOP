package com.jaeyong.oop.application.user.common

import kotlin.ConsistentCopyVisibility

/**
 * 회원가입 유스케이스 입력 커맨드.
 *
 * @property username 가입할 사용자명
 * @property password 가입할 평문 비밀번호
 */
@ConsistentCopyVisibility
data class JoinCommand private constructor(
    val username: String,
    val password: String
) {
    companion object {
        fun of(username: String, password: String): JoinCommand = JoinCommand(username, password)
    }
}
