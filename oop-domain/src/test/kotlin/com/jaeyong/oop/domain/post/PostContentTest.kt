package com.jaeyong.oop.domain.post

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PostContentTest {

    @Test
    fun `정상적인 내용으로 PostContent를 생성한다`() {
        val content = PostContent.of("게시글 내용")
        assertThat(content.value).isEqualTo("게시글 내용")
    }

    @Test
    fun `내용이 공백이면 예외를 던진다`() {
        assertThrows<IllegalArgumentException> { PostContent.of("   ") }
    }

    @Test
    fun `내용이 5000자를 초과하면 예외를 던진다`() {
        assertThrows<IllegalArgumentException> { PostContent.of("a".repeat(5001)) }
    }

    @Test
    fun `내용이 정확히 5000자이면 생성된다`() {
        val content = PostContent.of("a".repeat(5000))
        assertThat(content.value).hasSize(5000)
    }
}
