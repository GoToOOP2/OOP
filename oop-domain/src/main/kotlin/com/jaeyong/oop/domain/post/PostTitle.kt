package com.jaeyong.oop.domain.post

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode

/** 게시글 제목 VO, from()을 통해서만 생성 가능 */
data class PostTitle private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 100
        fun from(value: String): PostTitle {
            if (value.isBlank()) throw BaseException(ErrorCode.POST_TITLE_BLANK)
            if (value.length > MAX_LENGTH) throw BaseException(ErrorCode.POST_TITLE_TOO_LONG)
            return PostTitle(value)
        }
    }
}
