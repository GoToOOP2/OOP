package com.jaeyong.oop.domain.post

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("PostContent")
class PostContentTest {

    @Test
    @DisplayName("정상 내용으로 생성 성공")
    fun createWithValidContent() {
        // given
        val value = "정상 내용"

        // when
        val content = PostContent.of(value)

        // then
        assertEquals(value, content.value)
    }

    @Test
    @DisplayName("5000자 내용 생성 성공 - 경계값")
    fun createWithMaxLengthContent() {
        // given
        val value = "a".repeat(5000)

        // when
        val content = PostContent.of(value)

        // then
        assertEquals(5000, content.value.length)
    }

    @Test
    @DisplayName("공백 내용 생성 실패 - POST_CONTENT_BLANK")
    fun createWithBlankContent() {
        // given
        val value = "   "

        // when & then
        val ex = assertThrows<BaseException> { PostContent.of(value) }
        assertEquals(ErrorCode.POST_CONTENT_BLANK, ex.errorCode)
    }

    @Test
    @DisplayName("5001자 내용 생성 실패 - POST_CONTENT_TOO_LONG")
    fun createWithTooLongContent() {
        // given
        val value = "a".repeat(5001)

        // when & then
        val ex = assertThrows<BaseException> { PostContent.of(value) }
        assertEquals(ErrorCode.POST_CONTENT_TOO_LONG, ex.errorCode)
    }
}
