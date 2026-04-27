package com.jaeyong.oop.domain.post

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("PostTitle")
class PostTitleTest {

    @Test
    @DisplayName("정상 제목으로 생성 성공")
    fun createWithValidTitle() {
        // given
        val value = "정상 제목"

        // when
        val title = PostTitle.from(value)

        // then
        assertEquals(value, title.value)
    }

    @Test
    @DisplayName("100자 제목 생성 성공 - 경계값")
    fun createWithMaxLengthTitle() {
        // given
        val value = "a".repeat(100)

        // when
        val title = PostTitle.from(value)

        // then
        assertEquals(100, title.value.length)
    }

    @Test
    @DisplayName("공백 제목 생성 실패 - POST_TITLE_BLANK")
    fun createWithBlankTitle() {
        // given
        val value = "   "

        // when & then
        val ex = assertThrows<BaseException> { PostTitle.from(value) }
        assertEquals(ErrorCode.POST_TITLE_BLANK, ex.errorCode)
    }

    @Test
    @DisplayName("101자 제목 생성 실패 - POST_TITLE_TOO_LONG")
    fun createWithTooLongTitle() {
        // given
        val value = "a".repeat(101)

        // when & then
        val ex = assertThrows<BaseException> { PostTitle.from(value) }
        assertEquals(ErrorCode.POST_TITLE_TOO_LONG, ex.errorCode)
    }
}
