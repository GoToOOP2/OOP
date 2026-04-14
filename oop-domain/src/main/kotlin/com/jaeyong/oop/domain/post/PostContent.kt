package com.jaeyong.oop.domain.post

data class PostContent private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 5000
        fun of(value: String): PostContent {
            require(value.isNotBlank()) { "내용은 공백일 수 없습니다" }
            require(value.length <= MAX_LENGTH) { "내용은 ${MAX_LENGTH}자 이하여야 합니다" }
            return PostContent(value)
        }
    }
}
