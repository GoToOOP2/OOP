package com.jaeyong.oop.domain.post.vo

import kotlin.ConsistentCopyVisibility

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode

/**
 * 게시글 제목 Value Object.
 *
 * 1~255자 제한을 생성 시점에 강제한다.
 */
@ConsistentCopyVisibility
data class TitleVO private constructor(val value: String) {
    init {
        if (value.isBlank()) throw BaseException(ErrorCode.TITLE_BLANK)
        if (value.length > MAX_LENGTH) throw BaseException(ErrorCode.TITLE_TOO_LONG)
    }

    companion object {
        private const val MAX_LENGTH = 255

        fun from(value: String): TitleVO = TitleVO(value)
    }
}
