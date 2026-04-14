package com.jaeyong.oop.domain.post

data class PostTitle private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 100
        fun of(value: String): PostTitle {
            require(value.isNotBlank()) { "제목은 공백일 수 없습니다" }
            require(value.length <= MAX_LENGTH) { "제목은 ${MAX_LENGTH}자 이하여야 합니다" }
            return PostTitle(value)
        }
    }
}
