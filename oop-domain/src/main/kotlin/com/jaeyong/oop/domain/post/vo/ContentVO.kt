package com.jaeyong.oop.domain.post.vo

/**
 * 게시글 내용 Value Object.
 *
 * 빈 값 불가를 생성 시점에 강제한다. 길이 제한 없음 (DB: TEXT).
 */
data class ContentVO private constructor(val value: String) {
    init {
        require(value.isNotBlank()) { "내용은 비어있을 수 없습니다" }
    }

    companion object {
        fun from(value: String): ContentVO = ContentVO(value)
    }
}
