package com.jaeyong.oop.application.user.common

import kotlin.ConsistentCopyVisibility

/**
 * 토큰 검증 유스케이스 입력 커맨드.
 *
 * @property token 검증할 access token 문자열
 */
@ConsistentCopyVisibility
data class TokenValidationCommand private constructor(
    val token: String
) {
    companion object {
        fun of(token: String): TokenValidationCommand = TokenValidationCommand(token)
    }
}
