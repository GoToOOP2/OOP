package com.jaeyong.oop.domain.post.vo

/**
 * 게시글 제목 Value Object.
 *
 * 1~255자 제한을 생성 시점에 강제한다.
 */
data class TitleVO private constructor(val value: String) {
    init {
        require(value.isNotBlank()) { "제목은 비어있을 수 없습니다" }
        require(value.length <= 255) { "제목은 255자 이하여야 합니다" }
    }

    companion object {
        fun from(value: String): TitleVO = TitleVO(value)
    }
}
