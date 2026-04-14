package com.jaeyong.oop.domain.post

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PostTitleTest {

    @Test
    fun `정상적인 제목으로 PostTitle을 생성한다`() {
        val title = PostTitle.of("게시글 제목")
        assertThat(title.value).isEqualTo("게시글 제목")
    }

    @Test
    fun `제목이 공백이면 예외를 던진다`() {
        assertThrows<IllegalArgumentException> { PostTitle.of("   ") }
    }

    @Test
    fun `제목이 100자를 초과하면 예외를 던진다`() {
        assertThrows<IllegalArgumentException> { PostTitle.of("a".repeat(101)) }
    }

    @Test
    fun `제목이 정확히 100자이면 생성된다`() {
        val title = PostTitle.of("a".repeat(100))
        assertThat(title.value).hasSize(100)
    }
}
