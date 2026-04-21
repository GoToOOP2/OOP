package com.jaeyong.oop.application.user.result

import kotlin.ConsistentCopyVisibility

/**
 * 토큰 검증 유스케이스 결과.
 *
 * @property userId 유효한 토큰이면 사용자 ID, 유효하지 않으면 null
 */
@ConsistentCopyVisibility
data class TokenValidationResult private constructor(val userId: Long?) {
    companion object {
        fun of(userId: Long?): TokenValidationResult = TokenValidationResult(userId)
    }
}
