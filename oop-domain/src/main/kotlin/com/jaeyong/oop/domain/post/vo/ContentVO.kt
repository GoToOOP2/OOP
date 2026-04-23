package com.jaeyong.oop.domain.post.vo

import kotlin.ConsistentCopyVisibility

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode

/**
 * 게시글 내용 Value Object.
 *
 * 빈 값 불가를 생성 시점에 강제한다. 길이 제한 없음 (DB: TEXT).
 */
@ConsistentCopyVisibility
data class ContentVO private constructor(val value: String) {
    init {
        if (value.isBlank()) throw BaseException(ErrorCode.CONTENT_BLANK)
    }

    companion object {
        fun from(value: String): ContentVO = ContentVO(value)
    }
}
