package com.jaeyong.oop.domain.post

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode

/** 게시글 내용 VO, of()를 통해서만 생성 가능 */
data class PostContent private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 5000
        fun of(value: String): PostContent {
            if (value.isBlank()) throw BaseException(ErrorCode.POST_CONTENT_BLANK)
            if (value.length > MAX_LENGTH) throw BaseException(ErrorCode.POST_CONTENT_TOO_LONG)
            return PostContent(value)
        }
    }
}
